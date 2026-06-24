package com.example.hotelapp.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

/** Набор переиспользуемых анимаций переходов между экранами с единой длительностью и кривой. */
object NavTransitions {

    private const val DURATION = 350

    /** Общая спецификация анимации (длительность + кривая ускорения). */
    private fun <T> spec() = tween<T>(DURATION, easing = FastOutSlowInEasing)

    /** Мягкое перекрёстное затухание (по умолчанию и между вкладками). */
    fun softFadeIn(): EnterTransition = fadeIn(animationSpec = spec())

    fun softFadeOut(): ExitTransition = fadeOut(animationSpec = spec())

    /** Углубление: новый экран вдвигается справа с затуханием. */
    fun slideInFromRight(): EnterTransition =
        slideInHorizontally(
            initialOffsetX = { it / 3 },
            animationSpec = spec()
        ) + fadeIn(animationSpec = spec())

    /** Углубление: предыдущий экран слегка уходит влево с затуханием. */
    fun slideOutToLeft(): ExitTransition =
        slideOutHorizontally(
            targetOffsetX = { -it / 3 },
            animationSpec = spec()
        ) + fadeOut(animationSpec = spec())

    /** Назад: предыдущий экран возвращается слева с затуханием. */
    fun slideInFromLeft(): EnterTransition =
        slideInHorizontally(
            initialOffsetX = { -it / 3 },
            animationSpec = spec()
        ) + fadeIn(animationSpec = spec())

    /** Назад: текущий экран уходит вправо с затуханием. */
    fun slideOutToRight(): ExitTransition =
        slideOutHorizontally(
            targetOffsetX = { it / 3 },
            animationSpec = spec()
        ) + fadeOut(animationSpec = spec())

    // Сдвиги на всю ширину в стиле пейджера — используются между вкладками нижней навигации.

    fun tabSlideInFromRight(): EnterTransition =
        slideInHorizontally(initialOffsetX = { it }, animationSpec = spec())

    fun tabSlideInFromLeft(): EnterTransition =
        slideInHorizontally(initialOffsetX = { -it }, animationSpec = spec())

    fun tabSlideOutToLeft(): ExitTransition =
        slideOutHorizontally(targetOffsetX = { -it }, animationSpec = spec())

    fun tabSlideOutToRight(): ExitTransition =
        slideOutHorizontally(targetOffsetX = { it }, animationSpec = spec())
}
