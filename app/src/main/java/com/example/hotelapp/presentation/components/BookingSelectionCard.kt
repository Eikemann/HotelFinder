package com.example.hotelapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.hotelapp.R
import java.time.LocalDate


/**
 * Карточка выбора параметров брони: даты заезда/выезда (с кнопкой открытия календаря)
 * и счётчик количества гостей.
 */
@Composable
fun BookingSelectionCard(
    onCalendarClick: () -> Unit,
    checkInDate: LocalDate,
    checkOutDate: LocalDate
) {
    var guests by remember { mutableStateOf(1) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Заголовок
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(R.string.select_dates),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Заезд / Выезд
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DateBox(
                    title = stringResource(R.string.check_in),
                    value = checkInDate.toString(),
                    modifier = Modifier.weight(1f)
                )

                DateBox(
                    title = stringResource(R.string.check_out),
                    value = checkOutDate.toString(),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onCalendarClick) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = ""
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            Spacer(modifier = Modifier.height(12.dp))

            // Строка с количеством гостей
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.guests),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.guests_count, guests),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                GuestCounter(
                    count = guests,
                    onIncrement = { guests++ },
                    onDecrement = { if (guests > 1) guests-- }
                )
            }
        }
    }
}


/** Обрамлённый блок с подписью и значением одной даты (заезд или выезд). */
@Composable
private fun DateBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** Счётчик гостей с кнопками «−» и «+» (минимум 1 гость). */
@Composable
fun GuestCounter(
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onDecrement,
            enabled = count > 1,
            modifier = Modifier
                .size(36.dp)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
        ) {
            Text("-")
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = count.toString(),
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.width(12.dp))

        IconButton(
            onClick = onIncrement,
            modifier = Modifier
                .size(36.dp)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
        ) {
            Text("+")
        }
    }
}
