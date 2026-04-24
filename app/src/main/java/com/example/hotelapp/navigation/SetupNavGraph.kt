package com.example.hotelapp.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Dashboard.route,
    ) {
        dashboardNavGraph(navController = navController)
        hotelNavGraph(navController = navController)
    }
}
