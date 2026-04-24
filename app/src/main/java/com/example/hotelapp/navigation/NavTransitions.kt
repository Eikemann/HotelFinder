package com.example.hotelapp.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally


@OptIn(ExperimentalAnimationApi::class)
object NavTransitions {

    private const val ANIMATION_DURATION = 300

    fun slideInFromRight(): EnterTransition =
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(ANIMATION_DURATION)
        )

    fun slideOutToLeft(): ExitTransition =
        slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(ANIMATION_DURATION)
        )

    fun slideInFromLeft(): EnterTransition =
        slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween(ANIMATION_DURATION)
        )

    fun slideOutToRight(): ExitTransition =
        slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(ANIMATION_DURATION)
        )

    fun fadeIn(): EnterTransition =
        androidx.compose.animation.fadeIn(animationSpec = tween(ANIMATION_DURATION))

    fun fadeOut(): ExitTransition =
        androidx.compose.animation.fadeOut(animationSpec = tween(ANIMATION_DURATION))
}
