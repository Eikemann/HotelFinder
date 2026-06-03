package com.example.hotelapp.presentation.schedule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hotelapp.R
import com.example.hotelapp.domain.local.model.Schedule
import com.example.hotelapp.domain.remote.model.booking.OrderResponse
import com.example.hotelapp.presentation.components.scheduleList
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    navController: NavController,
    viewModel: ScheduleScreenViewModel = viewModel()
) {

    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    val calendarState = rememberSheetState()

    LaunchedEffect(Unit) {
        viewModel.loadMyOrders()
    }

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

    val schedules = viewModel.orders.map { it.toSchedule() }

    LazyColumn(modifier = Modifier.fillMaxSize()) {

        item {
            DatePickerRow(
                startDate = startDate,
                endDate = endDate,
                onDateButtonClick = { calendarState.show() }
            )
        }

        item { MyScheduleRow() }

        when {
            viewModel.isLoading -> item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            viewModel.errorMessage != null -> item {
                Text(
                    text = viewModel.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(24.dp)
                )
            }

            schedules.isEmpty() -> item {
                Text(
                    text = "You have no bookings yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(24.dp)
                )
            }

            else -> scheduleList(
                scheduleList = schedules,
                onScheduleCardClick = {}
            )
        }
    }
}

private fun OrderResponse.toSchedule(): Schedule {
    val dateRange = listOfNotNull(checkInDate, checkOutDate).joinToString(" → ")
    return Schedule(
        hotelName = propertyName ?: "Booking",
        pricePerNight = totalAmount?.let { "$" + "%.2f".format(it) } ?: "",
        checkInDate = dateRange,
        imageRes = R.drawable.hotelimage
    )
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
