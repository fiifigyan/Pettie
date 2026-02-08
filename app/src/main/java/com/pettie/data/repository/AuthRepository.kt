package com.pettie.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.pettie.data.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isLoggedIn: Boolean
        get() = currentUser != null

    val authState: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await().user!!
    }

    suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): Result<FirebaseUser> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("Registration failed")
        val firebaseUser = com.google.firebase.auth.UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        user.updateProfile(firebaseUser).await()
        createUserProfile(user.uid, email, displayName)
        user
    }

    private suspend fun createUserProfile(uid: String, email: String, displayName: String) {
        val user = User(
            id = uid,
            email = email,
            displayName = displayName,
            createdAt = System.currentTimeMillis()
        )
        firestore.collection(USERS_COLLECTION).document(uid).set(user).await()
    }

    suspend fun getUser(uid: String): Result<User?> = runCatching {
        firestore.collection(USERS_COLLECTION).document(uid).get().await()
            .toObject(User::class.java)
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun resetPassword(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }

    companion object {
        private const val USERS_COLLECTION = "users"
    }
}
