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
        transitionSpec = {
            defaultTransitionSpec(
                isPopTransition = isPopTransition,
                initialRoute = initialState,
                targetRoute = targetState,
            )
        },
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

private fun defaultTransitionSpec(
    isPopTransition: Boolean,
    initialRoute: Any?,
    targetRoute: Any?,
): ContentTransform {
    val transitionDirection: NavigationTransitionDirection =
        resolveTransitionDirection(
            initialRoute = initialRoute,
            targetRoute = targetRoute,
            isPopTransition = isPopTransition,
        )

    val enterInitialOffsetX: (Int) -> Int =
        when (transitionDirection) {
            NavigationTransitionDirection.FORWARD -> { fullWidth -> fullWidth }
            NavigationTransitionDirection.BACKWARD -> { fullWidth -> -fullWidth / 3 }
        }

    val exitTargetOffsetX: (Int) -> Int =
        when (transitionDirection) {
            NavigationTransitionDirection.FORWARD -> { fullWidth -> -fullWidth / 3 }
            NavigationTransitionDirection.BACKWARD -> { fullWidth -> fullWidth }
        }

    val enterTransition =
        slideInHorizontally(
            animationSpec = tween(durationMillis = NAVIGATION_SLIDE_DURATION_MILLIS),
            initialOffsetX = enterInitialOffsetX,
        ) + fadeIn(animationSpec = tween(durationMillis = NAVIGATION_FADE_DURATION_MILLIS))

    val exitTransition =
        slideOutHorizontally(
            animationSpec = tween(durationMillis = NAVIGATION_SLIDE_DURATION_MILLIS),
            targetOffsetX = exitTargetOffsetX,
        ) + fadeOut(animationSpec = tween(durationMillis = NAVIGATION_FADE_DURATION_MILLIS))

    return enterTransition togetherWith exitTransition
}

private fun resolveTransitionDirection(
    initialRoute: Any?,
    targetRoute: Any?,
    isPopTransition: Boolean,
): NavigationTransitionDirection {
    val initialTabOrder: Int? = resolveBottomTabOrder(route = initialRoute)
    val targetTabOrder: Int? = resolveBottomTabOrder(route = targetRoute)

    if (initialTabOrder != null && targetTabOrder != null && initialTabOrder != targetTabOrder) {
        return if (targetTabOrder > initialTabOrder) {
            NavigationTransitionDirection.FORWARD
        } else {
            NavigationTransitionDirection.BACKWARD
        }
    }

    return if (isPopTransition) {
        NavigationTransitionDirection.BACKWARD
    } else {
        NavigationTransitionDirection.FORWARD
    }
}

private fun resolveBottomTabOrder(route: Any?): Int? =
    when (route) {
        is HomeRoute -> 0
        is HistoryRoute -> 1
        is FriendsRoute -> 2
        is SettingRoute -> 3
        else -> null
    }

private enum class NavigationTransitionDirection {
    FORWARD,
    BACKWARD,
}
