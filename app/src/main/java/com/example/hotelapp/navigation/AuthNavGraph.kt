package com.example.hotelapp.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.hotelapp.presentation.auth.LoginScreen
import com.example.hotelapp.presentation.auth.RegisterScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Route.Login.route,
        route = Route.Auth.route
    ) {
        composable(Route.Login.route) {
            LoginScreen(
                onLoggedIn = { navController.toDashboard() },
                onNavigateToRegister = { navController.navigate(Route.Register.route) },
                onBrowseAsGuest = { navController.toDashboard() }
            )
        }
        composable(
            route = Route.Register.route,
            enterTransition = { NavTransitions.slideInFromRight() },
            exitTransition = { NavTransitions.slideOutToLeft() },
            popEnterTransition = { NavTransitions.slideInFromLeft() },
            popExitTransition = { NavTransitions.slideOutToRight() }
        ) {
            RegisterScreen(
                onRegistered = { navController.toDashboard() },
                onNavigateToLogin = { navController.popBackStack() },
                onBrowseAsGuest = { navController.toDashboard() }
            )
        }
    }
}

private fun NavHostController.toDashboard() {
    navigate(Route.Dashboard.route) {
        popUpTo(Route.Auth.route) { inclusive = true }
        launchSingleTop = true
    }
}
