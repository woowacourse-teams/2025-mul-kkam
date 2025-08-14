package com.mulkkam.ui.settingnotification.adapter

import com.mulkkam.ui.settingnotification.model.SettingType

sealed class SettingNotificationItem(
    val viewType: SettingViewType,
) {
    data class SwitchNotificationItem(
        val label: String,
        val isChecked: Boolean,
        val type: SettingType.Switch,
    ) : SettingNotificationItem(SettingViewType.SWITCH)

    data class NormalNotificationItem(
        val label: String,
        val type: SettingType.Normal,
    ) : SettingNotificationItem(SettingViewType.NORMAL)
}
