package com.pettie.data.model

data class PetListing(
    val id: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val title: String = "",
    val description: String = "",
    val species: String = "",
    val breed: String = "",
    val age: String = "",
    val gender: String = "",
    val price: Double = 0.0,
    val currency: String = "USD",
    val photoUrls: List<String> = emptyList(),
    val location: String = "",
    val status: ListingStatus = ListingStatus.AVAILABLE,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

enum class ListingStatus {
    AVAILABLE,
    RESERVED,
    SOLD
}
