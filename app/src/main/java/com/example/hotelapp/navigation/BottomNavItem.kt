package com.example.hotelapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem(
        route = Route.Home.route,
        icon = Icons.Default.Home,
        label = "Home"
    )
    object Search : BottomNavItem(
        route = Route.Search.route,
        icon = Icons.Default.Search,
        label = "Search"
    )

    object Schedule: BottomNavItem(
        route = Route.Schedule.route,
        icon = Icons.Default.DateRange,
        label = "Schedule"
    )

    companion object{
        val items = listOf(Home, Search, Schedule)
    }
}