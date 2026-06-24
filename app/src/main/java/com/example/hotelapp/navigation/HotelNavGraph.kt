package com.example.hotelapp.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.hotelapp.presentation.detail.DetailScreen
import com.example.hotelapp.presentation.schedule.BookingDetailScreen


/** Под-граф деталей: экран отеля (по hotelId) и экран деталей брони (по orderId). */
fun NavGraphBuilder.hotelNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Route.HotelDetail.route,
        route = Route.DetailGraph.route

    ) {
        composable(
            route = Route.HotelDetail.route,
            arguments = listOf(navArgument("hotelId") { nullable = false }),
            enterTransition = { NavTransitions.slideInFromRight() },
            exitTransition = { NavTransitions.slideOutToLeft() },
            popEnterTransition = { NavTransitions.slideInFromLeft() },
            popExitTransition = { NavTransitions.slideOutToRight() }
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotelId").orEmpty()
            DetailScreen(navController = navController, hotelId = hotelId)
        }

        composable(
            route = Route.BookingDetail.route,
            arguments = listOf(navArgument(Route.BookingDetail.ARG_ORDER_ID) {
                type = NavType.LongType
            }),
            enterTransition = { NavTransitions.slideInFromRight() },
            exitTransition = { NavTransitions.slideOutToLeft() },
            popEnterTransition = { NavTransitions.slideInFromLeft() },
            popExitTransition = { NavTransitions.slideOutToRight() }
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getLong(Route.BookingDetail.ARG_ORDER_ID) ?: 0L
            BookingDetailScreen(navController = navController, orderId = orderId)
        }
    }
}