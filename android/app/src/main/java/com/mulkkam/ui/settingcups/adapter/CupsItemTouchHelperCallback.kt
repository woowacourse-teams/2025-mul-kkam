package com.mulkkam.ui.settingcups.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class CupsItemTouchHelperCallback(
    private val adapter: ItemReorderListener,
) : ItemTouchHelper.Callback() {
    override fun isLongPressDragEnabled(): Boolean = false

    override fun isItemViewSwipeEnabled(): Boolean = false

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        val fromPos = source.adapterPosition
        val toPos = target.adapterPosition

        val adapter = recyclerView.adapter as? SettingCupsAdapter ?: return false
        if (adapter.getItemViewType(toPos) == SettingCupsViewType.ADD.ordinal) {
            return false
        }

        adapter.onItemMove(fromPos, toPos)
        return true
    }

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int,
    ) = Unit

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        super.clearView(recyclerView, viewHolder)
        adapter.onItemDrop()
    }
}
