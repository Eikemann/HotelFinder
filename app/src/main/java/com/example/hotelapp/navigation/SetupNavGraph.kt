package com.example.hotelapp.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun SetupNavGraph(navController: NavHostController) {
    // Browsing is public — everyone starts on the dashboard. Auth is only
    // required for actions like booking or writing a review, which route to
    // the Auth graph on demand.
    NavHost(
        navController = navController,
        startDestination = Route.Dashboard.route,
        enterTransition = { NavTransitions.softFadeIn() },
        exitTransition = { NavTransitions.softFadeOut() },
        popEnterTransition = { NavTransitions.softFadeIn() },
        popExitTransition = { NavTransitions.softFadeOut() },
    ) {
        authNavGraph(navController = navController)
        dashboardNavGraph(navController = navController)
        hotelNavGraph(navController = navController)
    }
}
