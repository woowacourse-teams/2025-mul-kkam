package com.mulkkam.ui.history.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray10
import com.mulkkam.ui.designsystem.MulkkamTheme

@Composable
fun GradientDonutChart(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 20.dp,
    backgroundColor: Color = Gray10,
    gradientColors: List<Color> =
        listOf(
            Color(0x80FFB7A5),
            Color(0xBFFFEBDD),
            Color(0xFFC9F0F8),
            Color(0x80FFB7A5),
        ),
    gradientStops: List<Float> = listOf(0.0f, 0.15f, 0.70f, 1.0f),
    rotationOffset: Float = -90f,
) {
    val normalizedProgress = progress.coerceIn(0f, 100f) / 100f
    val strokePx = with(LocalDensity.current) { strokeWidth.toPx() }

    Box(
        modifier = modifier.background(Color.Transparent).aspectRatio(1f),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = Stroke(width = strokePx, cap = StrokeCap.Round)
            val pairs =
                gradientStops.zip(gradientColors) { stop, color -> stop to color }.toTypedArray()

            val size = size.minDimension
            val rect =
                Rect(
                    offset =
                        Offset(
                            (this.size.width - size) / 2f + stroke.width / 2f,
                            (this.size.height - size) / 2f + stroke.width / 2f,
                        ),
                    size = Size(size - stroke.width, size - stroke.width),
                )

            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke,
                topLeft = rect.topLeft,
                size = rect.size,
            )

            drawArc(
                brush =
                    Brush.sweepGradient(
                        *pairs,
                        center = center,
                    ),
                startAngle = rotationOffset,
                sweepAngle = 360f * normalizedProgress,
                useCenter = false,
                style = stroke,
                topLeft = rect.topLeft,
                size = rect.size,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GradientDonutChartPreview() {
    MulkkamTheme {
        GradientDonutChart(progress = 10f, modifier = Modifier.size(200.dp))
    }
}
