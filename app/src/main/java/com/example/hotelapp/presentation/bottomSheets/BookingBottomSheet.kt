package com.example.hotelapp.presentation.bottomSheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelapp.R
import com.example.hotelapp.domain.remote.model.booking.RoomResponse
import com.example.hotelapp.presentation.booking.BookingViewModel
import com.example.hotelapp.presentation.components.BookingSelectionCard
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarTimeline
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Нижний лист бронирования: выбор комнаты и дат, расчёт стоимости и подтверждение брони
 * через [BookingViewModel]. После успешной брони показывает экран подтверждения.
 *
 * @param propertyId идентификатор объекта размещения, для которого бронируется комната
 * @param onClose закрытие листа
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingBottomSheet(
    propertyId: Int,
    onClose: () -> Unit,
    viewModel: BookingViewModel = viewModel()
) {
    LaunchedEffect(propertyId) {
        viewModel.loadRooms(propertyId.toLong())
    }

    val calendarState = rememberSheetState()
    var checkInDate by remember { mutableStateOf(LocalDate.now()) }
    var checkOutDate by remember { mutableStateOf(LocalDate.now().plusDays(1)) }

    val nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate)

    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
            // Прошедшие даты нельзя выбрать для заезда/выезда.
            disabledTimeline = CalendarTimeline.PAST
        ),
        selection = CalendarSelection.Period(
            onSelectRange = { startDate, endDate ->
                checkInDate = startDate
                checkOutDate = endDate
            }
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (viewModel.bookingConfirmed) {
            BookingConfirmed(onDone = onClose)
            return@Column
        }

        Text(
            text = stringResource(R.string.booking_details),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.booking_choose_room),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        when {
            viewModel.isLoadingRooms -> {
                CircularProgressIndicator()
            }
            viewModel.rooms.isEmpty() -> {
                Text(
                    text = stringResource(R.string.booking_no_rooms),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> {
                viewModel.rooms.forEach { room ->
                    RoomOption(
                        room = room,
                        selected = room.id == viewModel.selectedRoom?.id,
                        onClick = { viewModel.selectRoom(room) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        BookingSelectionCard(
            onCalendarClick = { calendarState.show() },
            checkInDate = checkInDate,
            checkOutDate = checkOutDate
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (nights > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.booking_nights, nights),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                viewModel.selectedRoom?.pricePerNight?.let { perNight ->
                    Text(
                        text = stringResource(R.string.price_per_night_amount, "%.0f".format(perNight)),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        } else {
            Text(
                text = stringResource(R.string.booking_pick_dates),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        viewModel.nightlyTotal(checkInDate, checkOutDate)?.let { total ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.booking_total), fontWeight = FontWeight.SemiBold)
                Text(
                    text = "$" + "%.2f".format(total),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        viewModel.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = { viewModel.confirmBooking(propertyId.toLong(), checkInDate, checkOutDate) },
            enabled = !viewModel.isSubmitting && viewModel.selectedRoom != null && nights > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (viewModel.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.height(22.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(stringResource(R.string.booking_confirm))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.common_cancel))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/** Карточка одного варианта комнаты с выделением выбора, вместимостью и ценой. */
@Composable
private fun RoomOption(
    room: RoomResponse,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = (room.roomType ?: stringResource(R.string.room_default)) +
                    (room.roomNumber?.let { " · #$it" } ?: ""),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = listOfNotNull(
                    room.capacity?.let { stringResource(R.string.room_sleeps, it) },
                    room.status?.takeIf { it != "AVAILABLE" }
                ).joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        room.pricePerNight?.let {
            Text(
                text = stringResource(R.string.price_per_night_amount, "%.0f".format(it)),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/** Экран успешного подтверждения брони с кнопкой завершения. */
@Composable
private fun BookingConfirmed(onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.booking_confirmed_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.booking_confirmed_sub),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text(stringResource(R.string.common_done))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
