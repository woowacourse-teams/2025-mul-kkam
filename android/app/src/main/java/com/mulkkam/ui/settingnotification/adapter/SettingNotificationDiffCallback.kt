package com.mulkkam.ui.settingnotification.adapter

import androidx.recyclerview.widget.DiffUtil

object SettingNotificationDiffCallback : DiffUtil.ItemCallback<SettingNotificationItem>() {
    override fun areItemsTheSame(
        oldItem: SettingNotificationItem,
        newItem: SettingNotificationItem,
    ): Boolean =
        when (oldItem) {
            is SettingNotificationItem.NormalNotificationItem ->
                newItem is SettingNotificationItem.NormalNotificationItem && oldItem.type == newItem.type

            is SettingNotificationItem.SwitchNotificationItem ->
                newItem is SettingNotificationItem.SwitchNotificationItem && oldItem.type == newItem.type
        }

    override fun areContentsTheSame(
        oldItem: SettingNotificationItem,
        newItem: SettingNotificationItem,
    ): Boolean = oldItem == newItem
}
