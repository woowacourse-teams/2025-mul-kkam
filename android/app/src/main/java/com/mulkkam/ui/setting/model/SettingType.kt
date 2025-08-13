package com.mulkkam.ui.setting.model

sealed interface SettingType {
    sealed interface Normal : SettingType {
        data object Nickname : Normal

        data object BodyInfo : Normal

        data object MyCup : Normal

        data object Goal : Normal

        data object Notification : Normal

        data object SystemNotification : Normal
    }

    sealed interface Switch : SettingType {
        data object Marketing : Switch

        data object Night : Switch
    }
}
