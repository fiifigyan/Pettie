package com.pettie.data.model

import com.google.firebase.firestore.DocumentId

data class Chat(
    @DocumentId val id: String = "",
    val userIds: List<String> = emptyList(),
    val lastMessage: Message? = null
)

data class Message(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0
)
