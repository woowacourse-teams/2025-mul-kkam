package com.mulkkam.ui.settingnotification.model

sealed interface SettingNotificationType {
    sealed interface Normal : SettingNotificationType {
        data object SystemNotification : Normal
    }

    sealed interface Switch : SettingNotificationType {
        data object MarketingNotification : Switch

        data object NightMode : Switch
    }
}
