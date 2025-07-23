package com.mulkkam.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import kotlin.math.min

class GradientDonutChartView(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {
    private var progress: Float = PROGRESS_DEFAULT
    private val sweepGradient: SweepGradient by lazy { createSweepGradient() }
    private val rect: RectF by lazy { createRect() }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = "#FFFAFAFA".toColorInt()
            style = Paint.Style.STROKE
            strokeWidth = CHART_STROKE_WIDTH
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = min(width, height).toFloat()
        canvas.drawArc(rect, CHART_START_ANGLE, CHART_FULL_ANGLE, false, backgroundPaint)

        paint.shader = sweepGradient
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = CHART_STROKE_WIDTH

        canvas.rotate(CHART_ROTATION_OFFSET, size / 2, size / 2)
        canvas.drawArc(rect, CHART_START_ANGLE, CHART_FULL_ANGLE * progress / PROGRESS_MAX_PERCENT, false, paint)
    }

    fun setProgress(targetProgress: Float) {
        val animator =
            ValueAnimator.ofFloat(progress, targetProgress).apply {
                this.duration = ANIMATION_DURATION_MS
                addUpdateListener {
                    progress = it.animatedValue as Float
                    invalidate()
                }
            }
        animator.start()
    }

    private fun createSweepGradient(): SweepGradient {
        val size = min(width, height).toFloat()

        return SweepGradient(
            size / 2,
            size / 2,
            intArrayOf(
                ColorUtils.setAlphaComponent("#FFB7A5".toColorInt(), (255 * 0.5f).toInt()),
                ColorUtils.setAlphaComponent(
                    "#FFEBDD".toColorInt(),
                    (255 * 0.75f).toInt(),
                ),
                "#C9F0F8".toColorInt(),
                ColorUtils.setAlphaComponent("#FFB7A5".toColorInt(), (255 * 0.5f).toInt()),
            ),
            floatArrayOf(
                0.0f,
                0.15f,
                0.70f,
                1.0f,
            ),
        )
    }

    private fun createRect(): RectF {
        val size = min(width, height).toFloat()

        return RectF(CHART_RECT_START, CHART_RECT_START, size, size).apply {
            inset(CHART_STROKE_WIDTH, CHART_STROKE_WIDTH)
        }
    }

    companion object {
        private const val PROGRESS_DEFAULT: Float = 0f
        private const val PROGRESS_MAX_PERCENT: Float = 100f

        private const val CHART_STROKE_WIDTH: Float = 20f
        private const val CHART_START_ANGLE: Float = 0f
        private const val CHART_FULL_ANGLE: Float = 360f
        private const val CHART_ROTATION_OFFSET: Float = -90f
        private const val CHART_RECT_START: Float = 0f

        private const val ANIMATION_DURATION_MS: Long = 600
    }
}
