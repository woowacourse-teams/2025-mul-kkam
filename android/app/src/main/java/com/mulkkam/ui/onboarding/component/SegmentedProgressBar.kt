package com.mulkkam.ui.onboarding.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.custom.progress.SegmentedProgressBarView
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Secondary100
import com.mulkkam.ui.designsystem.White

private const val MAX_SEGMENT_COUNT_DEFAULT: Int = 5
private const val PROCESS_MIN_VALUE: Int = 0
private const val PROCESS_OFFSET: Int = 1

private val SEGMENT_SPACING_DEFAULT: Dp = 8.dp
private val CORNER_RADIUS_DEFAULT: Dp = 8.dp

@Composable
fun SegmentedProgressBar(
    modifier: Modifier = Modifier,
    segmentCount: Int = MAX_SEGMENT_COUNT_DEFAULT,
    currentProgress: Int = PROCESS_MIN_VALUE,
    segmentSpacing: Dp = SEGMENT_SPACING_DEFAULT,
    activeColor: Color = Gray400,
    inactiveColor: Color = Gray200,
    cornerRadius: Dp = CORNER_RADIUS_DEFAULT,
) {
    val progress = currentProgress.coerceIn(0, segmentCount)

    Canvas(modifier = modifier) {
        if (segmentCount <= 0) return@Canvas

        val spacingPx = segmentSpacing.toPx()
        val radiusPx = cornerRadius.toPx()

        val totalSpacing = spacingPx * (segmentCount - PROCESS_OFFSET)
        val segmentWidth = (size.width - totalSpacing) / segmentCount
        val segmentHeight = size.height

        repeat(segmentCount) { index ->
            val left = index * (segmentWidth + spacingPx)

            drawRoundRect(
                color = if (index < progress) activeColor else inactiveColor,
                topLeft = Offset(left, 0f),
                size = Size(segmentWidth, segmentHeight),
                cornerRadius = CornerRadius(radiusPx, radiusPx),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SegmentedProgressBarPreview() {
    MulkkamTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            SegmentedProgressBar(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(2.dp),
                currentProgress = 3,
            )
        }
    }
}
