package com.example.hotelapp.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.hotelapp.data.auth.TokenManager

@Composable
fun SetupNavGraph(navController: NavHostController) {
    val startDestination = if (TokenManager.isLoggedIn) {
        Route.Dashboard.route
    } else {
        Route.Auth.route
    }
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        authNavGraph(navController = navController)
        dashboardNavGraph(navController = navController)
        hotelNavGraph(navController = navController)
    }
}
