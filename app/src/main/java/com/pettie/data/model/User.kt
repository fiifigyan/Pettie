package com.pettie.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val phone: String = "",
    val location: String = "",
    val createdAt: Long = 0L
)
