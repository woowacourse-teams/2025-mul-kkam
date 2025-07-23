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
    private var progress: Float = DEFAULT_PROGRESS
    private val sweepGradient: SweepGradient by lazy { createSweepGradient() }
    private val rect: RectF by lazy { createRect() }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = "#FFFAFAFA".toColorInt()
            style = Paint.Style.STROKE
            strokeWidth = STROKE_WIDTH
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = min(width, height).toFloat()
        canvas.drawArc(rect, ANGLE_START, ANGLE_FULL_CIRCLE, false, backgroundPaint)

        paint.shader = sweepGradient
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = STROKE_WIDTH

        canvas.rotate(ROTATION_OFFSET, size / 2, size / 2)
        canvas.drawArc(rect, ANGLE_START, ANGLE_FULL_CIRCLE * progress / MAX_PERCENTAGE, false, paint)
    }

    fun setProgress(targetProgress: Float) {
        val animator =
            ValueAnimator.ofFloat(progress, targetProgress).apply {
                this.duration = ANIMATION_DURATION
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

        return RectF(RECT_START, RECT_START, size, size).apply {
            inset(STROKE_WIDTH, STROKE_WIDTH)
        }
    }

    companion object {
        private const val DEFAULT_PROGRESS: Float = 0f

        private const val STROKE_WIDTH: Float = 100f
        private const val ANGLE_START: Float = 0f
        private const val ANGLE_FULL_CIRCLE: Float = 360f
        private const val ROTATION_OFFSET: Float = -90f
        private const val MAX_PERCENTAGE: Float = 100f
        private const val RECT_START: Float = 0f

        private const val ANIMATION_DURATION: Long = 600
    }
}
