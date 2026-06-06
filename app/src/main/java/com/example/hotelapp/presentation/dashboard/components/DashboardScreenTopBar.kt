package com.example.hotelapp.presentation.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.hotelapp.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreenTopBar(
    isLoggedIn: Boolean = false,
    onAuthAction: () -> Unit = {}
) {
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
                stringResource(R.string.top_current_location),
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
                    stringResource(R.string.top_location_value),
                    color = Color(0xFF0F0F0F),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = stringResource(R.string.cd_notifications),
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
            )
            Icon(
                imageVector = if (isLoggedIn) {
                    Icons.AutoMirrored.Filled.ExitToApp
                } else {
                    Icons.Default.AccountCircle
                },
                contentDescription = if (isLoggedIn) {
                    stringResource(R.string.cd_log_out)
                } else {
                    stringResource(R.string.cd_log_in)
                },
                modifier = Modifier
                    .padding(start = 12.dp)
                    .width(28.dp)
                    .height(28.dp)
                    .clickable { onAuthAction() }
            )
        }
}
}

