package com.mulkkam.ui.notification.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

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
}
