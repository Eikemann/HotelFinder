package com.example.hotelapp.domain.local.model

import androidx.compose.runtime.Immutable

// @Immutable tells the Compose compiler this type never changes after construction
// (all vals; `amenities` is never mutated). Without it, the `List` field marks
// Hotel as unstable, which makes HotelCard/CaruselHotelCard non-skippable and adds
// avoidable recomposition cost while scrolling.
@Immutable
data class Hotel(
    val id : Int,
    val name: String,
    val city: String,
    val location: String,
    val description : String,
    val pricePerNight: String,
    val rating: String,
    val imageRes: Int,
    val accommodationType : String,
    val imageUrl: String? = null,
    val amenities: List<String> = emptyList()

)
