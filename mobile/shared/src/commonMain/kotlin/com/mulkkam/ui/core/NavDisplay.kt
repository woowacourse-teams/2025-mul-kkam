package com.mulkkam.ui.core

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList

private const val ANIMATION_DURATION_MILLIS: Int = 300

@Composable
fun NavDisplay(
    backStack: SnapshotStateList<Any>,
    transitionSpec: ContentTransform =
        _root_ide_package_.com.mulkkam.ui.core
            .defaultTransitionSpec(),
    entryProvider: @Composable (route: Any) -> com.mulkkam.ui.core.NavEntry<*>,
) {
    val currentRoute = backStack.lastOrNull()

    AnimatedContent(
        targetState = currentRoute,
        transitionSpec = { transitionSpec },
        label = "NavDisplayAnimation",
    ) { route ->
        route?.let {
            entryProvider(it).content()
        }
    }
}

private fun defaultTransitionSpec(): ContentTransform {
    val enterTransition =
        slideInHorizontally(
            animationSpec = tween(durationMillis = _root_ide_package_.com.mulkkam.ui.core.ANIMATION_DURATION_MILLIS),
            initialOffsetX = { fullWidth -> fullWidth },
        ) + fadeIn(animationSpec = tween(durationMillis = _root_ide_package_.com.mulkkam.ui.core.ANIMATION_DURATION_MILLIS))

    val exitTransition =
        slideOutHorizontally(
            animationSpec = tween(durationMillis = _root_ide_package_.com.mulkkam.ui.core.ANIMATION_DURATION_MILLIS),
            targetOffsetX = { fullWidth -> -fullWidth },
        ) + fadeOut(animationSpec = tween(durationMillis = _root_ide_package_.com.mulkkam.ui.core.ANIMATION_DURATION_MILLIS))

    return enterTransition togetherWith exitTransition
}
