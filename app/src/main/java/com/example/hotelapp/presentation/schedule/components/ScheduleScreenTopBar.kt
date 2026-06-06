package com.example.hotelapp.presentation.schedule.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.hotelapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreenTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onBackButtonClick: () -> Unit
) {

    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = ""
                )
            }
        },
        title = {
            Text(
                stringResource(R.string.schedule_top_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
    )
}