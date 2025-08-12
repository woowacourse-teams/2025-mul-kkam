package com.mulkkam.ui.custom.progress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.mulkkam.R

class SegmentedProgressBarView(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {
    private val density = context.resources.displayMetrics.density

    private var segmentCount: Int = MAX_SEGMENT_COUNT_DEFAULT
    private var currentProgress: Int = PROCESS_MIN_VALUE
        set(value) {
            field = value.coerceIn(0, segmentCount)
            invalidate()
        }

    private var segmentSpacing: Float = SEGMENT_SPACING_DEFAULT * density
    private var activeColor: Int = ContextCompat.getColor(context, R.color.gray_400)
    private var inactiveColor: Int = ContextCompat.getColor(context, R.color.gray_200)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (segmentCount <= PROCESS_MIN_VALUE) return

        val totalSpacing = segmentSpacing * (segmentCount - PROCESS_OFFSET)
        val segmentWidth = (width - totalSpacing) / segmentCount.toFloat()
        val segmentHeight = height.toFloat()

        repeat(segmentCount) {
            val left = it * (segmentWidth + segmentSpacing)
            val right = left + segmentWidth
            paint.color = if (it < currentProgress) activeColor else inactiveColor
            canvas.drawRoundRect(
                left,
                0f,
                right,
                segmentHeight,
                CORNER_RADIUS_DEFAULT,
                CORNER_RADIUS_DEFAULT,
                paint,
            )
        }
    }

    fun setSegmentCount(count: Int) {
        segmentCount = count
        invalidate()
    }

    fun setProgress(process: Int) {
        currentProgress = process
        invalidate()
    }

    fun setActiveColor(
        @ColorRes color: Int,
    ) {
        activeColor = context.getColor(color)
    }

    fun setInactiveColor(
        @ColorRes color: Int,
    ) {
        inactiveColor = context.getColor(color)
    }

    companion object {
        private const val MAX_SEGMENT_COUNT_DEFAULT: Int = 4

        private const val PROCESS_MIN_VALUE: Int = 0
        private const val PROCESS_OFFSET: Int = 1

        private const val SEGMENT_SPACING_DEFAULT: Float = 8f

        private const val CORNER_RADIUS_DEFAULT: Float = 8f
    }
}
