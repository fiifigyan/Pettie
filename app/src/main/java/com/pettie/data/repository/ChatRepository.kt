package com.pettie.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pettie.data.model.Chat
import com.pettie.data.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val chatsCollection = firestore.collection("chats")

    fun getChats(): Flow<Result<List<Chat>>> = callbackFlow {
        val user = auth.currentUser ?: run {
            trySend(Result.failure(Exception("Not authenticated")))
            return@callbackFlow
        }

        val query = chatsCollection
            .whereArrayContains("userIds", user.uid)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                return@addSnapshotListener
            }
            if (snapshot == null) {
                trySend(Result.success(emptyList()))
                return@addSnapshotListener
            }

            val chats = snapshot.toObjects(Chat::class.java)
            trySend(Result.success(chats))
        }

        awaitClose { registration.remove() }
    }

    suspend fun getOrCreateChat(receiverId: String): Result<String> = runCatching {
        val user = auth.currentUser ?: error("Not authenticated")
        val senderId = user.uid

        if (senderId == receiverId) error("Cannot create chat with yourself")

        val userIds = listOf(senderId, receiverId).sorted()

        val querySnapshot = chatsCollection
            .whereEqualTo("userIds", userIds)
            .get()
            .await()

        if (querySnapshot.documents.isNotEmpty()) {
            querySnapshot.documents.first().id
        } else {
            val newChat = Chat(userIds = userIds)
            val documentReference = chatsCollection.add(newChat).await()
            documentReference.id
        }
    }

    suspend fun sendMessage(chatId: String, text: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: error("Not authenticated")
        val message = Message(
            senderId = user.uid,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        chatsCollection.document(chatId).collection("messages").add(message).await()
        chatsCollection.document(chatId).update("lastMessage", message).await()
    }

    fun getMessages(chatId: String): Flow<Result<List<Message>>> = callbackFlow {
        val query = chatsCollection.document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                return@addSnapshotListener
            }
            if (snapshot == null) {
                trySend(Result.success(emptyList()))
                return@addSnapshotListener
            }
            val messages = snapshot.toObjects(Message::class.java)
            trySend(Result.success(messages))
        }

        awaitClose { registration.remove() }
    }
}