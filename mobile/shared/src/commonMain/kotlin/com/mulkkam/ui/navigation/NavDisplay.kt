package com.mulkkam.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

private const val NAVIGATION_FADE_DURATION_MILLIS = 180
private const val NAVIGATION_SLIDE_DURATION_MILLIS = 240

@Composable
fun NavDisplay(
    backStack: SnapshotStateList<Any>,
    entryProvider: @Composable (route: Any) -> NavEntry<*>,
) {
    val currentRoute = backStack.lastOrNull()
    val previousBackStackSizeState = remember { mutableIntStateOf(backStack.size) }
    val isPopTransition = backStack.size < previousBackStackSizeState.intValue

    LaunchedEffect(backStack.size) {
        previousBackStackSizeState.intValue = backStack.size
    }

    AnimatedContent(
        targetState = currentRoute,
        transitionSpec = { defaultTransitionSpec(isPopTransition = isPopTransition) },
        label = "NavDisplayAnimation",
    ) { route ->
        route?.let {
            val routeOwner = remember(it) { RouteViewModelStoreOwner() }

            CompositionLocalProvider(LocalViewModelStoreOwner provides routeOwner) {
                entryProvider(it).content()
            }

            DisposableEffect(it) {
                onDispose {
                    if (!backStack.contains(it)) {
                        routeOwner.clear()
                    }
                }
            }
        }
    }
}

private fun defaultTransitionSpec(isPopTransition: Boolean): ContentTransform {
    val enterTransition =
        if (isPopTransition) {
            slideInHorizontally(
                animationSpec = tween(durationMillis = NAVIGATION_SLIDE_DURATION_MILLIS),
                initialOffsetX = { fullWidth -> -fullWidth / 3 },
            ) + fadeIn(animationSpec = tween(durationMillis = NAVIGATION_FADE_DURATION_MILLIS))
        } else {
            slideInHorizontally(
                animationSpec = tween(durationMillis = NAVIGATION_SLIDE_DURATION_MILLIS),
                initialOffsetX = { fullWidth -> fullWidth },
            ) + fadeIn(animationSpec = tween(durationMillis = NAVIGATION_FADE_DURATION_MILLIS))
        }

    val exitTransition =
        if (isPopTransition) {
            slideOutHorizontally(
                animationSpec = tween(durationMillis = NAVIGATION_SLIDE_DURATION_MILLIS),
                targetOffsetX = { fullWidth -> fullWidth },
            ) + fadeOut(animationSpec = tween(durationMillis = NAVIGATION_FADE_DURATION_MILLIS))
        } else {
            slideOutHorizontally(
                animationSpec = tween(durationMillis = NAVIGATION_SLIDE_DURATION_MILLIS),
                targetOffsetX = { fullWidth -> -fullWidth / 3 },
            ) + fadeOut(animationSpec = tween(durationMillis = NAVIGATION_FADE_DURATION_MILLIS))
        }

    return enterTransition togetherWith exitTransition
}
