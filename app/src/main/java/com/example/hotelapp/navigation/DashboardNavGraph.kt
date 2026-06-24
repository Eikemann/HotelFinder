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

// Порядок вкладок нижней навигации слева направо; определяет направление сдвига.
// Используется *базовый* маршрут Search, чтобы индекс не зависел от необязательного arg города.
private val tabOrder = listOf(Route.Home.route, Route.Search.base, Route.Schedule.route)

// Отрезаем query-аргументы (например, "search?city=Dubai" -> "search") перед сопоставлением.
private fun tabIndex(route: String?): Int = tabOrder.indexOf(route?.substringBefore("?"))

/**
 * Направление сдвига между вкладками: переход к вкладке правее вдвигает новый экран
 * справа (и наоборот). Возвращает null для не-вкладочной навигации, чтобы применился
 * переход NavHost по умолчанию (мягкое затухание).
 */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.tabEnter(): EnterTransition? {
    val from = tabIndex(initialState.destination.route)
    val to = tabIndex(targetState.destination.route)
    if (from == -1 || to == -1 || from == to) return null
    return if (to > from) NavTransitions.tabSlideInFromRight() else NavTransitions.tabSlideInFromLeft()
}

/** Зеркальный к [tabEnter] переход выхода для уходящего экрана вкладки. */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.tabExit(): ExitTransition? {
    val from = tabIndex(initialState.destination.route)
    val to = tabIndex(targetState.destination.route)
    if (from == -1 || to == -1 || from == to) return null
    return if (to > from) NavTransitions.tabSlideOutToLeft() else NavTransitions.tabSlideOutToRight()
}

/**
 * Под-граф главного раздела с тремя вкладками (Главная, Поиск, Брони).
 * Каждая вкладка оборачивается в [MainScreen] (общий каркас с нижней навигацией).
 */
fun NavGraphBuilder.dashboardNavGraph(navController: NavController){
    navigation(
        startDestination = Route.Home.route,
        route = Route.Dashboard.route
    ){
        tabOrder.forEach { tabRoute ->
            // У вкладки Поиск есть необязательный query-аргумент `city`; у остальных — нет.
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
