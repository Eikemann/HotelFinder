package com.example.hotelapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelapp.R

/** Where a booking sits relative to today — derived from its stay dates, not the server. */
enum class BookingTimeline { UPCOMING, ACTIVE, PAST }

/** UPCOMING before check-in, ACTIVE during the stay, PAST after check-out. */
fun timelineOf(checkIn: java.time.LocalDate?, checkOut: java.time.LocalDate?): BookingTimeline? {
    if (checkIn == null || checkOut == null) return null
    val today = java.time.LocalDate.now()
    return when {
        today.isBefore(checkIn) -> BookingTimeline.UPCOMING
        today.isAfter(checkOut) -> BookingTimeline.PAST
        else -> BookingTimeline.ACTIVE
    }
}

// Semantic status colors, kept theme-independent on purpose (like the gold stars / red heart).
private val PendingColor = Color(0xFFF59E0B)
private val ConfirmedColor = Color(0xFF16A34A)
private val CheckedInColor = Color(0xFF2563EB)
private val CheckedOutColor = Color(0xFF6B7280)
private val CancelledColor = Color(0xFFDC2626)

private fun statusColor(status: String?): Color = when (status?.uppercase()) {
    "PENDING" -> PendingColor
    "CONFIRMED" -> ConfirmedColor
    "CHECKED_IN" -> CheckedInColor
    "CHECKED_OUT" -> CheckedOutColor
    "CANCELLED" -> CancelledColor
    else -> CheckedOutColor
}

@Composable
private fun statusLabel(status: String?): String = when (status?.uppercase()) {
    "PENDING" -> stringResource(R.string.booking_status_pending)
    "CONFIRMED" -> stringResource(R.string.booking_status_confirmed)
    "CHECKED_IN" -> stringResource(R.string.booking_status_checked_in)
    "CHECKED_OUT" -> stringResource(R.string.booking_status_checked_out)
    "CANCELLED" -> stringResource(R.string.booking_status_cancelled)
    else -> status ?: stringResource(R.string.booking_status_pending)
}

@Composable
private fun timelineLabel(timeline: BookingTimeline): String = when (timeline) {
    BookingTimeline.UPCOMING -> stringResource(R.string.booking_timeline_upcoming)
    BookingTimeline.ACTIVE -> stringResource(R.string.booking_timeline_active)
    BookingTimeline.PAST -> stringResource(R.string.booking_timeline_past)
}

/** Color-coded pill showing the server-side booking status. */
@Composable
fun StatusBadge(status: String?, modifier: Modifier = Modifier) {
    val color = statusColor(status)
    Text(
        text = statusLabel(status),
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

/** Subtle Upcoming/Active/Past label derived from the stay dates. Hidden for cancelled bookings. */
@Composable
fun TimelineLabel(timeline: BookingTimeline?, status: String?, modifier: Modifier = Modifier) {
    if (timeline == null || status?.uppercase() == "CANCELLED") return
    Text(
        text = timelineLabel(timeline),
        color = if (timeline == BookingTimeline.ACTIVE) CheckedInColor else CheckedOutColor,
        fontSize = 11.sp,
        modifier = modifier
    )
}
