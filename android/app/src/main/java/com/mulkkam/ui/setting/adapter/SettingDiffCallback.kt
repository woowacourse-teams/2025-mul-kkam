package com.mulkkam.ui.setting.adapter

import androidx.recyclerview.widget.DiffUtil

object SettingDiffCallback : DiffUtil.ItemCallback<SettingItem>() {
    override fun areItemsTheSame(
        oldItem: SettingItem,
        newItem: SettingItem,
    ): Boolean =
        when (oldItem) {
            is SettingItem.TitleItem ->
                newItem is SettingItem.TitleItem && oldItem.title == newItem.title

            is SettingItem.NormalItem ->
                newItem is SettingItem.NormalItem && oldItem.type == newItem.type

            is SettingItem.DividerItem ->
                newItem is SettingItem.DividerItem
        }

    override fun areContentsTheSame(
        oldItem: SettingItem,
        newItem: SettingItem,
    ): Boolean = oldItem == newItem
}
