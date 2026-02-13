package com.pettie.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.pettie.data.model.PetListing
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetListingRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    fun getRecentListings(limit: Long = 50): Flow<Result<List<PetListing>>> = callbackFlow {
        val query = listingsCollection()
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error)).isSuccess
                return@addSnapshotListener
            }
            if (snapshot == null) {
                trySend(Result.success(emptyList())).isSuccess
                return@addSnapshotListener
            }
            val listings = snapshot.documents.mapNotNull { it.toObject(PetListing::class.java)?.copy(id = it.id) }
            trySend(Result.success(listings)).isSuccess
        }

        awaitClose { registration.remove() }
    }

    fun getListingsByUser(userId: String): Flow<Result<List<PetListing>>> = callbackFlow {
        val query = listingsCollection()
            .whereEqualTo("sellerId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error)).isSuccess
                return@addSnapshotListener
            }
            if (snapshot == null) {
                trySend(Result.success(emptyList())).isSuccess
                return@addSnapshotListener
            }
            val listings = snapshot.documents.mapNotNull { it.toObject(PetListing::class.java)?.copy(id = it.id) }
            trySend(Result.success(listings)).isSuccess
        }

        awaitClose { registration.remove() }
    }

    fun getListing(id: String): Flow<Result<PetListing?>> = callbackFlow {
        val docRef = listingsCollection().document(id)
        val registration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error)).isSuccess
                return@addSnapshotListener
            }
            if (snapshot == null || !snapshot.exists()) {
                trySend(Result.success(null)).isSuccess
                return@addSnapshotListener
            }
            val listing = snapshot.toObject(PetListing::class.java)?.copy(id = snapshot.id)
            trySend(Result.success(listing)).isSuccess
        }

        awaitClose { registration.remove() }
    }

    suspend fun createListing(
        title: String,
        description: String,
        species: String,
        breed: String,
        age: String,
        gender: String,
        price: Double,
        location: String,
        imageUris: List<Uri>
    ): Result<Unit> = runCatching {
        val user = auth.currentUser ?: error("Not authenticated")
        
        val imageUrls = uploadImages(imageUris)
        
        val now = System.currentTimeMillis()
        val listing = PetListing(
            sellerId = user.uid,
            sellerName = user.displayName.orEmpty(),
            title = title,
            description = description,
            species = species,
            breed = breed,
            age = age,
            gender = gender,
            price = price,
            location = location,
            photoUrls = imageUrls,
            createdAt = now,
            updatedAt = now
        )
        listingsCollection().add(listing).await()
    }

    private suspend fun uploadImages(uris: List<Uri>): List<String> {
        val imageUrls = mutableListOf<String>()
        val storageRef = storage.reference.child("listing_images")
        
        for (uri in uris) {
            val fileName = UUID.randomUUID().toString()
            val fileRef = storageRef.child(fileName)
            fileRef.putFile(uri).await()
            val downloadUrl = fileRef.downloadUrl.await().toString()
            imageUrls.add(downloadUrl)
        }
        
        return imageUrls
    }

    private fun listingsCollection() =
        firestore.collection(LISTINGS_COLLECTION)

    companion object {
        private const val LISTINGS_COLLECTION = "listings"
    }
}
