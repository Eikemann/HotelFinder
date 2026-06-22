package com.example.hotelapp.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.hotelapp.navigation.BottomNavItem

@Composable
fun BottomNavigationBar(
    navController: NavController,
) {

    val items = BottomNavItem.items

    NavigationBar {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        // Strip optional query args (e.g. "search?city=Dubai") so the Search tab
        // still shows as selected when opened with a city filter.
        val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("?")

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // 🔥 IMPORTANT: avoid multiple copies
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(item.icon, contentDescription = stringResource(item.labelRes))
                },
                label = {
                    Text(stringResource(item.labelRes))
                }
            )
        }
    }
}
