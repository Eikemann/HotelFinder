package com.example.hotelapp.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.hotelapp.presentation.dashboard.DashboardScreen
import com.example.hotelapp.presentation.mainScreen.MainScreen
import com.example.hotelapp.presentation.schedule.ScheduleScreen
import com.example.hotelapp.presentation.search.SearchScreen

fun NavGraphBuilder.dashboardNavGraph(navController: NavController){
    navigation(
        startDestination = Route.Home.route,
        route = Route.Dashboard.route
    ){
        composable(Route.Home.route){
            MainScreen(navController){
                DashboardScreen(navController = navController)
            }
        }
        composable(Route.Search.route){
            MainScreen(navController) {
                SearchScreen(navController = navController)
            }
        }
        composable(Route.Schedule.route){
            MainScreen(navController) {
                ScheduleScreen(navController = navController)
            }
        }
    }
}