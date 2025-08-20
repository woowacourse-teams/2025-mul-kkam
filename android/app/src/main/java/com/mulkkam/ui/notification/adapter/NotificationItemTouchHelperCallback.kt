package com.mulkkam.ui.notification.adapter

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.R

class NotificationItemTouchHelperCallback(
    private val itemSwipeListener: ItemSwipeListener,
) : ItemTouchHelper.Callback() {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean = false

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int,
    ) {
        val position = viewHolder.adapterPosition
        itemSwipeListener.onItemSwipe(position)
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int = makeMovementFlags(0, ItemTouchHelper.LEFT)

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) return
        val itemView = viewHolder.itemView

        val background = drawBackground(c, recyclerView, itemView, dX)
        drawDeleteIcon(c, recyclerView, background)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawBackground(
        c: Canvas,
        recyclerView: RecyclerView,
        itemView: View,
        dX: Float,
    ): RectF {
        val paint =
            Paint().apply {
                color = ContextCompat.getColor(recyclerView.context, R.color.secondary_200)
            }

        val background =
            RectF(
                itemView.right.toFloat() + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat(),
            )
        c.drawRect(background, paint)
        return background
    }

    private fun drawDeleteIcon(
        c: Canvas,
        recyclerView: RecyclerView,
        background: RectF,
    ) {
        val icon = ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_notification_delete)
        icon?.let {
            val density = recyclerView.context.resources.displayMetrics.density
            val iconSize = (24 * density).toInt()

            val centerX = background.centerX().toInt()
            val centerY = background.centerY().toInt()

            val left = centerX - iconSize / 2
            val top = centerY - iconSize / 2
            val right = centerX + iconSize / 2
            val bottom = centerY + iconSize / 2

            it.setBounds(left, top, right, bottom)
            it.draw(c)
        }
    }
}
