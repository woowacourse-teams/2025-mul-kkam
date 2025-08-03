package com.mulkkam.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.mulkkam.R

class SegmentedProgressBarView(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {
    private val density = context.resources.displayMetrics.density

    var segmentCount: Int = 4
    var currentProgress: Int = 0
        set(value) {
            field = value.coerceIn(0, segmentCount)
            invalidate()
        }

    var segmentSpacing: Float = 16f * density
    var activeColor: Int = ContextCompat.getColor(context, R.color.gray_400)
    var inactiveColor: Int = ContextCompat.getColor(context, R.color.gray_200)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (segmentCount <= 0) return

        val totalSpacing = segmentSpacing * (segmentCount - 1)
        val segmentWidth = (width - totalSpacing) / segmentCount.toFloat()
        val segmentHeight = height.toFloat()

        for (i in 0 until segmentCount) {
            val left = i * (segmentWidth + segmentSpacing)
            val right = left + segmentWidth
            paint.color = if (i < currentProgress) activeColor else inactiveColor
            canvas.drawRoundRect(left, 0f, right, segmentHeight, 8f, 8f, paint)
        }
    }
}
