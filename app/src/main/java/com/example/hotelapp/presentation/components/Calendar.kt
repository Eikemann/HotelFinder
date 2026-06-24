package com.example.hotelapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.hotelapp.R
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * Полноразмерный календарь на месяц: заголовок с переключением месяцев, строка дней недели
 * и сетка дат 7×6 с выделением выбранного дня.
 */
@Composable
fun FullCalendar(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onMonthChange: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val days = remember(currentMonth) {
        generateCalendarDays(currentMonth)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        CalendarHeader(
            currentMonth = currentMonth,
            onPrevious = { onMonthChange(currentMonth.minusMonths(1)) },
            onNext = { onMonthChange(currentMonth.plusMonths(1)) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        DaysOfWeekRow()

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            userScrollEnabled = false, // отключаем прокрутку сетки (вложена в скролл — иначе краш)
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(days) { date ->
                CalendarDay(
                    date = date,
                    isSelected = date == selectedDate,
                    isCurrentMonth = date?.month == currentMonth.month,
                    onClick = {
                        if (date != null) onDateSelected(date)
                    }
                )
            }
        }
    }
}
/** Заголовок календаря: название месяца с годом и кнопки «предыдущий/следующий месяц». */
@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentMonth.month
                .getDisplayName(TextStyle.FULL, Locale.getDefault()) +
                    " ${currentMonth.year}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Row {
            IconButton(onClick = onPrevious) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = stringResource(R.string.cd_prev_month))
            }
            IconButton(onClick = onNext) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = stringResource(R.string.cd_next_month))
            }
        }
    }
}


/** Строка сокращённых названий дней недели над сеткой дат. */
@Composable
private fun DaysOfWeekRow() {
    val days = listOf("S", "M", "T", "W", "T", "F", "S")

    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEach {
            Text(
                text = it,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Одна ячейка календаря: число дня с выделением выбора; пустая для дней-заполнителей (null). */
@Composable
fun CalendarDay(
    date: LocalDate?,
    isSelected: Boolean,
    isCurrentMonth: Boolean,
    onClick: () -> Unit
) {
    if (date == null) {
        Spacer(modifier = Modifier.aspectRatio(1f))
        return
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                isCurrentMonth -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.outline
            },
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}


/**
 * Формирует список из 42 ячеек (6 недель) для сетки месяца: ведущие/замыкающие пустые ячейки
 * (null) выравнивают первый день по дню недели и дополняют сетку до полного размера.
 */
fun generateCalendarDays(month: YearMonth): List<LocalDate?> {
    val firstDay = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()
    val startOffset = firstDay.dayOfWeek.value % 7

    val totalCells = 42
    val dates = mutableListOf<LocalDate?>()

    repeat(startOffset) {
        dates.add(null)
    }

    for (day in 1..daysInMonth) {
        dates.add(month.atDay(day))
    }

    while (dates.size < totalCells) {
        dates.add(null)
    }

    return dates
}

