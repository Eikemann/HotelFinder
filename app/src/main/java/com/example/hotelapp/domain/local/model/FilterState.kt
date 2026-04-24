package com.example.hotelapp.domain.local.model

data class FilterState(
    val country: String? = null,
    val sort: String? = null,
    val priceRange: ClosedFloatingPointRange<Float> = 0f..1000f,
    val starRating: Int? = null,
    val facilities: Set<String> = emptySet(),
    val accommodationTypes: Set<String> = emptySet()
)
