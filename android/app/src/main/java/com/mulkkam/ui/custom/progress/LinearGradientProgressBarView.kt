package com.mulkkam.ui.custom.progress

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.mulkkam.R

/**
 * 커스텀 뷰(LinearGradientProgressBarView)의 그림자 요소가 부모 뷰 경계를 넘어 자연스럽게 보이도록 하기 위해 부모 뷰그룹의 clipChildren 옵션을 false로 설정해야 합니다
 */

class LinearGradientProgressBarView(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {
    private val density = context.resources.displayMetrics.density

    private var progress: Float = PROGRESS_INITIAL_PERCENT

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            val baseColor = ContextCompat.getColor(context, R.color.black)
            val shadowColor = ColorUtils.setAlphaComponent(baseColor, (255 * 0.25).toInt())
            setShadowLayer(
                4 * density,
                0 * density,
                4 * density,
                shadowColor,
            )
        }

    private var linearGradient: LinearGradient? = null
    private val rect: RectF by lazy { createRect() }

    private var cornerRadius: Float = CORNER_RADIUS_DEFAULT

    private fun createRect(): RectF = RectF(0f, 0f, width.toFloat(), height.toFloat())

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        rect.set(CHART_RECT_START, CHART_RECT_START, width, height)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, backgroundPaint)

        settingPaint()

        val progressWidth = width * progress / PROGRESS_MAX_PERCENT
        rect.set(CHART_RECT_START, CHART_RECT_START, progressWidth, height)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
    }

    private fun settingPaint() {
        paint.shader = linearGradient
        paint.style = Paint.Style.FILL
    }

    fun setProgress(targetProgress: Float) {
        val animator =
            ValueAnimator.ofFloat(progress, targetProgress).apply {
                duration = ANIMATION_DURATION_MS
                addUpdateListener {
                    progress = it.animatedValue.toString().toFloatOrNull() ?: return@addUpdateListener
                    invalidate()
                }
            }
        animator.start()
    }

    fun setPaintGradient(newGradient: LinearGradient) {
        linearGradient = newGradient
        paint.shader = linearGradient
    }

    fun setBackgroundPaintColor(
        @ColorRes color: Int,
    ) {
        backgroundPaint.color = context.getColor(color)
    }

    fun setCornerRadius(radius: Float) {
        cornerRadius = radius * density
    }

    companion object {
        private const val PROGRESS_INITIAL_PERCENT: Float = 0f
        private const val PROGRESS_MAX_PERCENT: Float = 100f

        private const val CORNER_RADIUS_DEFAULT: Float = 0f

        private const val CHART_RECT_START: Float = 0f

        private const val ANIMATION_DURATION_MS: Long = 600L
    }
}
