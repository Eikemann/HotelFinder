package com.example.hotelapp.presentation.schedule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hotelapp.R
import com.example.hotelapp.domain.local.model.Schedule
import com.example.hotelapp.presentation.components.scheduleList
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(navController: NavController) {

    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    val calendarState = rememberSheetState()

    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Period(
            onSelectRange = { start, end ->
                startDate = start
                endDate = end
            }
        )
    )


    LazyColumn(modifier = Modifier.fillMaxSize()) {

        item {
            DatePickerRow(
                startDate = startDate,
                endDate = endDate,
                onDateButtonClick = { calendarState.show() }
            )
        }

        item { MyScheduleRow() }

        scheduleList(
            scheduleList = scheduleList, onScheduleCardClick = {})
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerRow(
    startDate: LocalDate?,
    endDate: LocalDate?,
    onDateButtonClick: () -> Unit
) {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yy")

    @Composable
    fun DateButton(label: String, date: LocalDate?, endPadding: Int) {
        Column {
            Text(
                label,
                color = Color(0xFF1E1E1E),
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp, end = endPadding.dp)
            )
            OutlinedButton(
                onClick = onDateButtonClick,
                border = BorderStroke(0.dp, Color.Transparent),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                modifier = Modifier
                    .border(width = 1.dp, color = Color(0xFFD9D9D9), shape = RoundedCornerShape(8.dp))
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(8.dp))
            ) {
                Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
                    Text(
                        text = date?.format(formatter) ?: "Select",
                        color = Color(0xFF1E1E1E),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "")
                }
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(bottom = 23.dp, start = 24.dp, end = 24.dp)
            .fillMaxWidth()
    ) {
        DateButton(label = "Start date", date = startDate, endPadding = 46)
        DateButton(label = "End date", date = endDate, endPadding = 55)
    }
}

@Composable
private fun MyScheduleRow(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 16.dp, start = 21.dp, end = 21.dp)
            .fillMaxWidth()
    ) {
        Text(
            "My Schedule",
            color = Color(0xFF0F0F0F),
            fontSize = 16.sp,
        )
        Text(
            "See all",
            color = Color(0xFF4C4DDC),
            fontSize = 14.sp,
        )
    }
}

val scheduleList = listOf(
    Schedule(
        hotelName = "Hotel",
        pricePerNight = "34.33",
        checkInDate = "12.12.12",
        imageRes = R.drawable.hotelimage
    ), Schedule(
        hotelName = "Hotel",
        pricePerNight = "34.33",
        checkInDate = "12.12.12",
        imageRes = R.drawable.hotelimage
    ), Schedule(
        hotelName = "Hotel",
        pricePerNight = "34.33",
        checkInDate = "12.12.12",
        imageRes = R.drawable.hotelimage
    ), Schedule(
        hotelName = "Hotel",
        pricePerNight = "34.33",
        checkInDate = "12.12.12",
        imageRes = R.drawable.hotelimage
    ), Schedule(
        hotelName = "Hotel",
        pricePerNight = "34.33",
        checkInDate = "12.12.12",
        imageRes = R.drawable.hotelimage
    ), Schedule(
        hotelName = "Hotel",
        pricePerNight = "34.33",
        checkInDate = "12.12.12",
        imageRes = R.drawable.hotelimage
    ), Schedule(
        hotelName = "Hotel",
        pricePerNight = "34.33",
        checkInDate = "12.12.12",
        imageRes = R.drawable.hotelimage
    )
)