package com.example.hotelapp.domain.local.model

import java.time.LocalDate

data class Schedule(
    val id : Long = 0,
    val hotelName : String,
    val pricePerNight : String,
    val checkInDate : String,
    val imageRes: Int,
    val imageUrl: String? = null,
    // Raw backend order status (PENDING/CONFIRMED/CHECKED_IN/CHECKED_OUT/CANCELLED).
    val status: String? = null,
    // Parsed stay dates, used to derive the Upcoming/Active/Past timeline.
    val checkIn: LocalDate? = null,
    val checkOut: LocalDate? = null
)
