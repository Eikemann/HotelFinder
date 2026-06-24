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

/** Нижняя панель навигации с вкладками из [BottomNavItem]; подсвечивает активную вкладку. */
@Composable
fun BottomNavigationBar(
    navController: NavController,
) {

    val items = BottomNavItem.items

    NavigationBar {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        // Отрезаем необязательные query-аргументы (например, "search?city=Dubai"), чтобы вкладка
        // «Поиск» оставалась выделенной даже при открытии с фильтром города.
        val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("?")

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // ВАЖНО: не плодим копии одного экрана в стеке, сохраняя его состояние.
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
