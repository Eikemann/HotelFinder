package com.example.hotelapp.presentation.components

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import com.example.hotelapp.domain.local.model.Schedule

fun LazyListScope.scheduleList(
    scheduleList : List<Schedule>,
    onScheduleCardClick: () -> Unit
){
    items(scheduleList){item ->
        HotelScheduleCard(
            schedule = item
        )
    }
}