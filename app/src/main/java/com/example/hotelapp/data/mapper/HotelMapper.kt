package com.example.hotelapp.data.mapper

import com.example.hotelapp.domain.local.model.Hotel
import com.example.hotelapp.domain.remote.model.HotelResponseItem

fun HotelResponseItem.toHotel(): Hotel{
    return Hotel(
        id = id,
        name = name,
        location = location,
        description = description,
        pricePerNight = price_per_night.toString(),
        rating = rating.toString(),
        imageRes = 0,
        accommodationType = "Hotel",
        imageUrl = image_url
    )
}