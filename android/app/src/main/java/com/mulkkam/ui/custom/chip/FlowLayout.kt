package com.mulkkam.ui.custom.chip

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

open class FlowLayout
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
    ) : ViewGroup(context, attrs) {
        private var horizontalSpacing = 0
        private var verticalSpacing = 0

        fun setSpacing(
            horizontal: Int,
            vertical: Int,
        ) {
            horizontalSpacing = horizontal
            verticalSpacing = vertical
            requestLayout()
        }

        override fun onMeasure(
            widthMeasureSpec: Int,
            heightMeasureSpec: Int,
        ) {
            var width = 0
            var height = 0
            var rowWidth = 0
            var rowHeight = 0

            val maxWidth = MeasureSpec.getSize(widthMeasureSpec)

            for (index in 0 until childCount) {
                val child = getChildAt(index)
                measureChild(child, widthMeasureSpec, heightMeasureSpec)

                if (rowWidth + child.measuredWidth > maxWidth) {
                    width = maxOf(width, rowWidth)
                    height += rowHeight + verticalSpacing
                    rowWidth = child.measuredWidth
                    rowHeight = child.measuredHeight
                } else {
                    rowWidth += child.measuredWidth + horizontalSpacing
                    rowHeight = maxOf(rowHeight, child.measuredHeight)
                }
            }

            width = maxOf(width, rowWidth)
            height += rowHeight

            setMeasuredDimension(width, height)
        }

        override fun onLayout(
            p0: Boolean,
            p1: Int,
            p2: Int,
            p3: Int,
            p4: Int,
        ) {
            var x = 0
            var y = 0
            var rowHeight = 0
            val maxWidth = measuredWidth

            for (index in 0 until childCount) {
                val child = getChildAt(index)
                val childWidth = child.measuredWidth
                val childHeight = child.measuredHeight

                if (x + childWidth > maxWidth) {
                    x = 0
                    y += rowHeight + verticalSpacing
                    rowHeight = 0
                }

                child.layout(x, y, x + childWidth, y + childHeight)
                x += childWidth + horizontalSpacing
                rowHeight = maxOf(rowHeight, childHeight)
            }
        }
    }
