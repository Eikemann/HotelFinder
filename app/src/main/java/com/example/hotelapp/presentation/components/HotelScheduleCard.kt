package com.example.hotelapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelapp.domain.local.model.Schedule

@Composable
fun HotelScheduleCard(
    modifier: Modifier = Modifier,
    schedule: Schedule
) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Card(
                modifier = Modifier.padding(12.dp),
                shape = RoundedCornerShape(6)
            ) {
                Image(
                    painter = painterResource(schedule.imageRes),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(75.dp)
                        .width(75.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            schedule.hotelName,
                            color = Color(0xFF0F0F0F),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = ""
                            )
                            Text(
                                text = schedule.checkInDate,
                                color = Color(0xFF939393),
                                fontSize = 12.sp,
                            )
                        }
                    }
                    Row {
                        Text(
                            text = schedule.pricePerNight,
                            color = Color(0xFF4C4DDC),
                        )
                        Text(
                            text = " /night",
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "",
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        }

    }
}