package com.example.hotelapp.presentation.components

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import com.example.hotelapp.domain.local.model.Schedule

fun LazyListScope.scheduleList(
    scheduleList : List<Schedule>,
    onDelete: (Long) -> Unit
){
    items(scheduleList, key = { it.id }){item ->
        HotelScheduleCard(
            schedule = item,
            onDelete = { onDelete(item.id) }
        )
    }
}