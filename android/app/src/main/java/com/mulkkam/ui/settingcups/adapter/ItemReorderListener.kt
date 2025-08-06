package com.mulkkam.ui.settingcups.adapter

interface ItemReorderListener {
    fun onItemMove(
        fromPosition: Int,
        toPosition: Int,
    )

    fun onItemDrop()
}
