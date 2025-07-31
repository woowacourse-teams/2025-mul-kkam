package com.mulkkam.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import kotlin.math.min

class GradientDonutChartView(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {
    private val density = context.resources.displayMetrics.density

    private var progress: Float = PROGRESS_INITIAL_PERCENT
    private var strokePx: Float = CHART_STROKE_DEFAULT_DP

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = strokePx
        }

    private var sweepGradient: SweepGradient? = null
    private val rect: RectF by lazy { createRect() }

    private fun createRect(): RectF {
        val size = min(width, height).toFloat()

        return RectF(CHART_RECT_START, CHART_RECT_START, size, size).apply {
            inset(strokePx, strokePx)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = min(width, height).toFloat()
        canvas.drawArc(rect, CHART_START_ANGLE, CHART_FULL_ANGLE, false, backgroundPaint)

        settingPaint()

        canvas.rotate(CHART_ROTATION_OFFSET, size / 2, size / 2)
        canvas.drawArc(
            rect,
            CHART_START_ANGLE,
            CHART_FULL_ANGLE * progress / PROGRESS_MAX_PERCENT,
            false,
            paint,
        )
    }

    private fun settingPaint() {
        paint.shader = sweepGradient
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokePx
    }

    fun setProgress(targetProgress: Float) {
        val animator =
            ValueAnimator.ofFloat(progress, targetProgress).apply {
                this.duration = ANIMATION_DURATION_MS
                addUpdateListener {
                    progress = it.animatedValue.toString().toFloatOrNull() ?: return@addUpdateListener
                    invalidate()
                }
            }
        animator.start()
    }

    fun setStroke(newStrokeWidth: Float) {
        strokePx = newStrokeWidth * density

        backgroundPaint.strokeWidth = strokePx
        paint.strokeWidth = strokePx
    }

    fun setPaintGradient(newGradient: SweepGradient) {
        sweepGradient = newGradient
        paint.shader = sweepGradient
    }

    fun setBackgroundPaintColor(
        @ColorRes color: Int,
    ) {
        backgroundPaint.color = context.getColor(color)
    }

    fun setPaintColor(
        @ColorRes color: Int,
    ) {
        paint.shader = null
        paint.color = context.getColor(color)
    }

    companion object {
        private const val PROGRESS_INITIAL_PERCENT: Float = 0f
        private const val PROGRESS_MAX_PERCENT: Float = 100f

        private const val CHART_START_ANGLE: Float = 0f
        private const val CHART_FULL_ANGLE: Float = 360f
        private const val CHART_ROTATION_OFFSET: Float = -90f
        private const val CHART_RECT_START: Float = 0f
        private const val CHART_STROKE_DEFAULT_DP: Float = 0f

        private const val ANIMATION_DURATION_MS: Long = 600L
    }
}
