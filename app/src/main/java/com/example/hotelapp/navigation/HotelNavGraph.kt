package com.example.hotelapp.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.hotelapp.presentation.detail.DetailScreen


fun NavGraphBuilder.hotelNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Route.HotelDetail.route,
        route = Route.DetailGraph.route

    ) {
        composable(
            route = Route.HotelDetail.route,
            arguments = listOf(navArgument("hotelId") { nullable = false })
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotelId").orEmpty()
            DetailScreen(navController = navController, hotelId = hotelId)
        }
    }
}