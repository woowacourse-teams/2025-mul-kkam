package com.mulkkam.ui.settingcups.adapter

interface ItemTouchHelperAdapter {
    fun onItemMove(
        fromPosition: Int,
        toPosition: Int,
    )

    fun onItemDrop()
}
