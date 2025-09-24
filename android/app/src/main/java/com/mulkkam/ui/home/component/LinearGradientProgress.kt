package com.mulkkam.ui.home.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.White

@Composable
fun LinearGradientProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    backgroundColor: Color = White,
    shadowElevation: Dp = 6.dp,
    gradientColors: List<Color> =
        listOf(
            Color(0xFFFFB7A5),
            Color(0xFFFFEBDD),
            Color(0xFFC9F0F8),
            Color(0xFFC9F0F8),
        ),
    gradientStops: List<Float> = listOf(0f, 0.15f, 0.70f, 1f),
    animationDuration: Int = 600,
) {
    val shape = RoundedCornerShape(cornerRadius)
    val targetProgress = (progress / 100f).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = animationDuration),
    )

    Box(
        modifier =
            modifier
                .shadow(shadowElevation, shape, clip = false)
                .background(backgroundColor, shape)
                .clip(shape),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val filledWidth = size.width * animatedProgress

            val pairs = gradientStops.zip(gradientColors) { stop, color -> stop to color }.toTypedArray()
            val brush =
                Brush.linearGradient(
                    *pairs,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    tileMode = TileMode.Clamp,
                )

            val cornerRadiusPx = minOf(cornerRadius.toPx(), size.height / 2f)

            if (filledWidth > 0f) {
                drawRoundRect(
                    brush = brush,
                    size = Size(filledWidth, size.height),
                    cornerRadius =
                        CornerRadius(cornerRadiusPx, cornerRadiusPx),
                )
            }
        }
    }
}
