package com.example.hotelapp.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

/**
 * Корневой навигационный граф приложения: объединяет под-графы авторизации,
 * главного экрана и деталей отеля, задавая общие анимации переходов.
 */
@Composable
fun SetupNavGraph(navController: NavHostController) {
    // Просмотр публичен — все стартуют с главного экрана. Авторизация нужна лишь для
    // действий вроде брони или написания отзыва, которые ведут в граф Auth по требованию.
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
