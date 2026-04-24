package com.example.hotelapp.presentation.components

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import com.example.hotelapp.domain.local.model.Hotel

fun LazyListScope.popularList(
    populars: List<Hotel>,
    onCardClick: (String) -> Unit
) {
    items(populars) { item ->
        HotelCard(
            hotel = item,
            onCardClick = {onCardClick(item.id.toString())}
        )
    }
}