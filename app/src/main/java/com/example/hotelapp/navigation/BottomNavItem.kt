package com.example.hotelapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.hotelapp.R

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val labelRes: Int
) {
    object Home : BottomNavItem(
        route = Route.Home.route,
        icon = Icons.Default.Home,
        labelRes = R.string.nav_home
    )
    object Search : BottomNavItem(
        route = Route.Search.base,
        icon = Icons.Default.Search,
        labelRes = R.string.nav_search
    )

    object Schedule: BottomNavItem(
        route = Route.Schedule.route,
        icon = Icons.Default.DateRange,
        labelRes = R.string.nav_schedule
    )

    companion object{
        val items = listOf(Home, Search, Schedule)
    }
}