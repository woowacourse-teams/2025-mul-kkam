package com.mulkkam.ui.settingnotification.adapter

import com.mulkkam.ui.settingnotification.model.SettingNotificationType

sealed class SettingNotificationItem(
    val viewType: SettingNotificationViewType,
) {
    data class SwitchNotificationItem(
        val label: String,
        val isChecked: Boolean,
        val type: SettingNotificationType.Switch,
    ) : SettingNotificationItem(SettingNotificationViewType.SWITCH)

    data class NormalNotificationItem(
        val label: String,
        val type: SettingNotificationType.Normal,
    ) : SettingNotificationItem(SettingNotificationViewType.NORMAL)
}
