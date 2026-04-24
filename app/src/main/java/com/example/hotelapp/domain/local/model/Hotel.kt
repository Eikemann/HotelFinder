package com.example.hotelapp.domain.local.model

data class Hotel(
    val id : Int,
    val name: String,
    val location: String,
    val description : String,
    val pricePerNight: String,
    val rating: String,
    val imageRes: Int,
    val accommodationType : String,
    val imageUrl: String? = null

)
