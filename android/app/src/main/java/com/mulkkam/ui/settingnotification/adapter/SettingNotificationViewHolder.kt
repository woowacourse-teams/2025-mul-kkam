package com.mulkkam.ui.settingnotification.adapter

import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class SettingNotificationViewHolder<ITEM : SettingNotificationItem, VB : ViewBinding>(
    protected val binding: VB,
) : RecyclerView.ViewHolder(binding.root) {
    protected lateinit var item: ITEM

    @CallSuper
    open fun bind(item: ITEM) {
        this.item = item
    }
}
