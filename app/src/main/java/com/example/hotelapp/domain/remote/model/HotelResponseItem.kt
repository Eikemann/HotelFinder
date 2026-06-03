package com.example.hotelapp.domain.remote.model

/**
 * Mirrors the backend `PropertyResponse` (simple-service). Field names match the
 * JSON keys exactly so Gson can map them without `@SerializedName`.
 */
data class HotelResponseItem(
    val id: Int,
    val name: String,
    val description: String?,
    val address: String?,
    val city: String?,
    val country: String?,
    val phone: String?,
    val email: String?,
    val starRating: Int?,
    val propertyType: String?,
    val imageUrl: String?,
    val amenities: List<Amenity> = emptyList(),
    val rating: Double?,
    val reviewCount: Int?,
    val pricePerNight: Double?,
    val createdAt: String?
)
