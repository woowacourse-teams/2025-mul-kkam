package com.mulkkam.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

@Composable
fun NavDisplay(
    backStack: SnapshotStateList<Any>,
    entryProvider: @Composable (route: Any) -> NavEntry<*>,
) {
    val currentRoute = backStack.lastOrNull()

    AnimatedContent(
        targetState = currentRoute,
        transitionSpec = { defaultTransitionSpec() },
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
    val transitionDuration = 180

    val enterTransition = fadeIn(animationSpec = tween(durationMillis = transitionDuration))
    val exitTransition = fadeOut(animationSpec = tween(durationMillis = transitionDuration))

    return enterTransition togetherWith exitTransition
}
