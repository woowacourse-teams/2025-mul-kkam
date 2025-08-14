package com.mulkkam.ui.settingnotification.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class SettingViewHolder<ITEM : SettingNotificationItem, VB : ViewBinding>(
    protected val binding: VB,
) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item: ITEM)
}
