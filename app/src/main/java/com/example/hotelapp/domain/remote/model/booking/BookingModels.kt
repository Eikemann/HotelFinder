package com.example.hotelapp.domain.remote.model.booking

/** Mirrors backend `RoomResponse`. */
data class RoomResponse(
    val id: Long,
    val propertyId: Long?,
    val roomNumber: String?,
    val roomType: String?,
    val capacity: Int?,
    val pricePerNight: Double?,
    val status: String?
)

/** Mirrors backend `CreateOrderRequest`. Dates are ISO `yyyy-MM-dd` strings. */
data class CreateOrderRequest(
    val propertyId: Long,
    val roomId: Long,
    val checkInDate: String,
    val checkOutDate: String
)

/** Mirrors backend `OrderResponse`. */
data class OrderResponse(
    val id: Long,
    val propertyId: Long?,
    val propertyName: String?,
    val roomId: Long?,
    val roomNumber: String?,
    val userId: Long?,
    val userFullName: String?,
    val checkInDate: String?,
    val checkOutDate: String?,
    val totalAmount: Double?,
    val orderStatus: String?,
    val createdAt: String?
)
