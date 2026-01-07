package com.mulkkam.ui.settingcups.adapter

import androidx.recyclerview.widget.DiffUtil

object SettingCupsDiffCallback : DiffUtil.ItemCallback<SettingCupsItem>() {
    override fun areItemsTheSame(
        oldItem: SettingCupsItem,
        newItem: SettingCupsItem,
    ): Boolean = oldItem.rank == newItem.rank

    override fun areContentsTheSame(
        oldItem: SettingCupsItem,
        newItem: SettingCupsItem,
    ): Boolean = oldItem == newItem
}
