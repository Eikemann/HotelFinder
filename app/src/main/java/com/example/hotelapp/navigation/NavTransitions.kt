package com.example.hotelapp.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

object NavTransitions {

    private const val DURATION = 350

    private fun <T> spec() = tween<T>(DURATION, easing = FastOutSlowInEasing)

    /** Soft cross-fade, used between bottom-nav tabs. */
    fun softFadeIn(): EnterTransition = fadeIn(animationSpec = spec())

    fun softFadeOut(): ExitTransition = fadeOut(animationSpec = spec())

    /** Drill-in: new screen slides in from the right while fading. */
    fun slideInFromRight(): EnterTransition =
        slideInHorizontally(
            initialOffsetX = { it / 3 },
            animationSpec = spec()
        ) + fadeIn(animationSpec = spec())

    /** Drill-in: previous screen slides slightly left while fading. */
    fun slideOutToLeft(): ExitTransition =
        slideOutHorizontally(
            targetOffsetX = { -it / 3 },
            animationSpec = spec()
        ) + fadeOut(animationSpec = spec())

    /** Back: previous screen returns from the left while fading. */
    fun slideInFromLeft(): EnterTransition =
        slideInHorizontally(
            initialOffsetX = { -it / 3 },
            animationSpec = spec()
        ) + fadeIn(animationSpec = spec())

    /** Back: current screen slides out to the right while fading. */
    fun slideOutToRight(): ExitTransition =
        slideOutHorizontally(
            targetOffsetX = { it / 3 },
            animationSpec = spec()
        ) + fadeOut(animationSpec = spec())
}
