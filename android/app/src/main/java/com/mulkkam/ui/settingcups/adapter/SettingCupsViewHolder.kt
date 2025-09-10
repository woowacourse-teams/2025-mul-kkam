package com.mulkkam.ui.settingcups.adapter

import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class SettingCupsViewHolder<ITEM : SettingCupsItem, BINDING : ViewBinding>(
    protected val binding: BINDING,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var item: SettingCupsItem

    @CallSuper
    open fun bind(item: ITEM) {
        this.item = item
    }
}
