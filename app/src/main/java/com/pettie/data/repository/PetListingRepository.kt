package com.pettie.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pettie.data.model.PetListing
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetListingRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
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

    suspend fun createListing(
        title: String,
        description: String,
        species: String,
        price: Double,
        location: String
    ): Result<Unit> = runCatching {
        val user = auth.currentUser ?: error("Not authenticated")
        val now = System.currentTimeMillis()
        val listing = PetListing(
            sellerId = user.uid,
            sellerName = user.displayName.orEmpty(),
            title = title,
            description = description,
            species = species,
            price = price,
            location = location,
            createdAt = now,
            updatedAt = now
        )
        listingsCollection().add(listing).await()
    }

    private fun listingsCollection() =
        firestore.collection(LISTINGS_COLLECTION)

    companion object {
        private const val LISTINGS_COLLECTION = "listings"
    }
}

