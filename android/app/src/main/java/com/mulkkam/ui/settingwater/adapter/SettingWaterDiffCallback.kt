package com.mulkkam.ui.settingwater.adapter

import androidx.recyclerview.widget.DiffUtil

object SettingWaterDiffCallback : DiffUtil.ItemCallback<SettingWaterItem>() {
    override fun areItemsTheSame(
        oldItem: SettingWaterItem,
        newItem: SettingWaterItem,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: SettingWaterItem,
        newItem: SettingWaterItem,
    ): Boolean = oldItem == newItem
}
