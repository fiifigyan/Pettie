package com.pettie.data.model

import com.google.firebase.firestore.DocumentId

data class Favorite(
    @DocumentId val id: String = "",
    val userId: String = "",
    val listingId: String = ""
)
