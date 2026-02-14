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
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

private const val ANIMATION_DURATION_MILLIS: Int = 300

@Composable
fun NavDisplay(
    backStack: SnapshotStateList<Any>,
    entryProvider: @Composable (route: Any) -> NavEntry<*>,
) {
    val currentRoute = backStack.lastOrNull()

    AnimatedContent(
        targetState = currentRoute,
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

private fun defaultTransitionSpec(): ContentTransform {
    val enterTransition =
        slideInHorizontally(
            animationSpec = tween(durationMillis = ANIMATION_DURATION_MILLIS),
            initialOffsetX = { fullWidth -> fullWidth },
        ) + fadeIn(animationSpec = tween(durationMillis = ANIMATION_DURATION_MILLIS))

    val exitTransition =
        slideOutHorizontally(
            animationSpec = tween(durationMillis = ANIMATION_DURATION_MILLIS),
            targetOffsetX = { fullWidth -> -fullWidth },
        ) + fadeOut(animationSpec = tween(durationMillis = ANIMATION_DURATION_MILLIS))

    return enterTransition togetherWith exitTransition
}
