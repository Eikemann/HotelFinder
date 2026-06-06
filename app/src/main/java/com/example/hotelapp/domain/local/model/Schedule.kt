package com.example.hotelapp.domain.local.model

data class Schedule(
    val id : Long = 0,
    val hotelName : String,
    val pricePerNight : String,
    val checkInDate : String,
    val imageRes: Int,
    val imageUrl: String? = null
)
