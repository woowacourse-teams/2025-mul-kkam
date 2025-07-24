package com.mulkkam.ui.settingwater.adapter

import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class SettingWaterViewHolder<ITEM : SettingWaterItem, BINDING : ViewBinding>(
    protected val binding: BINDING,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var item: SettingWaterItem

    @CallSuper
    open fun bind(item: ITEM) {
        this.item = item
    }
}
