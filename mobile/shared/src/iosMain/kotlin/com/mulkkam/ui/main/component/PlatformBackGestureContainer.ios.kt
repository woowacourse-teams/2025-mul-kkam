package com.mulkkam.ui.main.component

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.abs

private val BACK_GESTURE_EDGE_WIDTH = 28.dp
private val BACK_GESTURE_MIN_DISTANCE = 32.dp

@Composable
actual fun PlatformBackGestureContainer(
    enabled: Boolean,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val edgeWidthPixel = remember(density) { with(density) { BACK_GESTURE_EDGE_WIDTH.toPx() } }
    val minimumSwipeDistancePixel = remember(density) { with(density) { BACK_GESTURE_MIN_DISTANCE.toPx() } }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .pointerInput(enabled, edgeWidthPixel, minimumSwipeDistancePixel) {
                    if (!enabled) {
                        return@pointerInput
                    }

                    awaitEachGesture {
                        val firstDown = awaitFirstDown(requireUnconsumed = false)
                        if (firstDown.position.x > edgeWidthPixel) {
                            return@awaitEachGesture
                        }

                        var accumulatedHorizontalDragDistance = 0f
                        var accumulatedVerticalDragDistance = 0f
                        var shouldFinishGesture = false
                        var isBackGestureTriggered = false

                        while (!shouldFinishGesture) {
                            val pointerEvent = awaitPointerEvent(pass = PointerEventPass.Initial)
                            val pointerChange =
                                pointerEvent.changes.firstOrNull { change ->
                                    change.id == firstDown.id
                                } ?: continue

                            val deltaX = pointerChange.position.x - pointerChange.previousPosition.x
                            val deltaY = pointerChange.position.y - pointerChange.previousPosition.y
                            accumulatedHorizontalDragDistance += deltaX
                            accumulatedVerticalDragDistance += abs(deltaY)

                            val isDraggingToRight = accumulatedHorizontalDragDistance > 0f
                            val isHorizontalDominant =
                                accumulatedHorizontalDragDistance > accumulatedVerticalDragDistance
                            val isDistanceEnough = accumulatedHorizontalDragDistance > minimumSwipeDistancePixel

                            if (isDraggingToRight && isHorizontalDominant && isDistanceEnough) {
                                pointerEvent.changes.forEach { it.consume() }
                                onBack()
                                isBackGestureTriggered = true
                                shouldFinishGesture = true
                            }

                            if (!pointerChange.pressed) {
                                shouldFinishGesture = true
                            }
                        }

                        if (isBackGestureTriggered) {
                            return@awaitEachGesture
                        }
                    }
                },
    ) {
        content()
    }
}
