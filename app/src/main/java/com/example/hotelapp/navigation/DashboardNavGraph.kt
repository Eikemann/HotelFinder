package com.example.hotelapp.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.hotelapp.presentation.dashboard.DashboardScreen
import com.example.hotelapp.presentation.mainScreen.MainScreen
import com.example.hotelapp.presentation.schedule.ScheduleScreen
import com.example.hotelapp.presentation.search.SearchScreen

// Left-to-right order of the bottom-nav tabs; determines the slide direction.
// Uses the Search *base* route so the index lookup ignores its optional city arg.
private val tabOrder = listOf(Route.Home.route, Route.Search.base, Route.Schedule.route)

// Strip any query args (e.g. "search?city=Dubai" -> "search") before matching.
private fun tabIndex(route: String?): Int = tabOrder.indexOf(route?.substringBefore("?"))

/**
 * Slide direction between tabs: moving to a tab further right slides the new
 * screen in from the right (and vice versa). Returns null for non-tab
 * navigation so the NavHost default (soft fade) applies.
 */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.tabEnter(): EnterTransition? {
    val from = tabIndex(initialState.destination.route)
    val to = tabIndex(targetState.destination.route)
    if (from == -1 || to == -1 || from == to) return null
    return if (to > from) NavTransitions.tabSlideInFromRight() else NavTransitions.tabSlideInFromLeft()
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.tabExit(): ExitTransition? {
    val from = tabIndex(initialState.destination.route)
    val to = tabIndex(targetState.destination.route)
    if (from == -1 || to == -1 || from == to) return null
    return if (to > from) NavTransitions.tabSlideOutToLeft() else NavTransitions.tabSlideOutToRight()
}

fun NavGraphBuilder.dashboardNavGraph(navController: NavController){
    navigation(
        startDestination = Route.Home.route,
        route = Route.Dashboard.route
    ){
        tabOrder.forEach { tabRoute ->
            // The Search tab carries an optional `city` query arg; other tabs don't.
            val isSearch = tabRoute == Route.Search.base
            composable(
                route = if (isSearch) Route.Search.route else tabRoute,
                arguments = if (isSearch) {
                    listOf(navArgument(Route.Search.ARG_CITY) {
                        type = NavType.StringType
                        defaultValue = ""
                    })
                } else {
                    emptyList()
                },
                enterTransition = { tabEnter() },
                exitTransition = { tabExit() },
                popEnterTransition = { tabEnter() },
                popExitTransition = { tabExit() }
            ){ backStackEntry ->
                MainScreen(navController) {
                    when (tabRoute) {
                        Route.Home.route -> DashboardScreen(navController = navController)
                        Route.Search.base -> {
                            val city = backStackEntry.arguments
                                ?.getString(Route.Search.ARG_CITY).orEmpty()
                            SearchScreen(navController = navController, initialCity = city)
                        }
                        else -> ScheduleScreen(navController = navController)
                    }
                }
            }
        }
    }
}
