package com.mulkkam.ui.setting.model

sealed interface SettingType {
    sealed interface Normal : SettingType {
        data object Nickname : Normal

        data object BodyInfo : Normal

        data object AccountInfo : Normal

        data object MyCup : Normal

        data object Goal : Normal

        data object PushNotification : Normal

        data object Feedback : Normal

        data object Terms : Normal
    }
}
