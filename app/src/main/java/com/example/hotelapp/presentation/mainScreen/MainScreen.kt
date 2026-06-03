package com.example.hotelapp.presentation.mainScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.hotelapp.data.auth.TokenManager
import com.example.hotelapp.navigation.BottomNavItem
import com.example.hotelapp.navigation.Route
import com.example.hotelapp.presentation.components.BottomNavigationBar
import com.example.hotelapp.presentation.dashboard.components.DashboardScreenTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, content: @Composable () -> Unit) {


    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()

    val items = BottomNavItem.items

    val showBottomBar = items.any { it.route == currentRoute }

    val onLogout: () -> Unit = {
        scope.launch { TokenManager.clear() }
        navController.navigate(Route.Auth.route) {
            popUpTo(Route.Dashboard.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            if (currentRoute != BottomNavItem.Search.route) {
                DashboardScreenTopBar(onLogout = onLogout)
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            content()
        }

    }
}
