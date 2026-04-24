package com.example.hotelapp.domain.remote.model

data class HotelResponseItem(
    val amenities: List<Amenity>,
    val description: String,
    val id: Int,
    val image_url: String,
    val lattitude: String,
    val location: String,
    val longitude: String,
    val name: String,
    val price_per_night: Int,
    val rating: Int
)