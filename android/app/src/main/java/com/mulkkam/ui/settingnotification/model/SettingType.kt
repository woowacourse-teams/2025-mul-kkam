package com.mulkkam.ui.settingnotification.model

sealed interface SettingType {
    sealed interface Normal : SettingType {
        data object SystemNotification : Normal
    }

    sealed interface Switch : SettingType {
        data object MarketingNotification : Switch

        data object NightMode : Switch
    }
}
