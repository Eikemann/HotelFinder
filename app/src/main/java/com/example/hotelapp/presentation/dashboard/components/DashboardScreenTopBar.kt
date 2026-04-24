package com.example.hotelapp.presentation.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreenTopBar() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 32.dp, start = 14.dp, end = 24.dp, top = 50.dp)
            .fillMaxWidth()
    ) {
        Column(
        ) {
            Text(
                "Current location",
                color = Color(0xFF939393),
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(bottom = 8.dp, end = 59.dp)
            )
            Row(
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .width(20.dp)
                        .height(20.dp)
                )
                Text(
                    "Wallace, Australia",
                    color = Color(0xFF0F0F0F),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "",
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
        )
    }
}

