package com.example.hotelapp.data.mapper

import com.example.hotelapp.domain.local.model.Hotel
import com.example.hotelapp.domain.remote.model.HotelResponseItem

fun HotelResponseItem.toHotel(): Hotel {
    return Hotel(
        id = id,
        name = name,
        // Backend stores address parts separately; compose a display location.
        location = listOfNotNull(city, country)
            .filter { it.isNotBlank() }
            .joinToString(", "),
        description = description.orEmpty(),
        // "From" price = cheapest room for the property (may be null if no rooms).
        pricePerNight = pricePerNight?.let { "%.0f".format(it) }.orEmpty(),
        // Prefer the review-derived rating; fall back to the star rating.
        rating = (rating ?: starRating?.toDouble())?.toString().orEmpty(),
        imageRes = 0,
        accommodationType = propertyType.orEmpty(),
        imageUrl = imageUrl,
        amenities = amenities.map { it.name }
    )
}
