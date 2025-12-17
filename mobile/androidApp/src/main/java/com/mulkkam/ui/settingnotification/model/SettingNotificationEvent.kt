package com.mulkkam.ui.settingnotification.model

sealed interface SettingNotificationEvent {
    data object Error : SettingNotificationEvent

    data class MarketingUpdated(
        val agreed: Boolean,
    ) : SettingNotificationEvent

    data class NightUpdated(
        val agreed: Boolean,
    ) : SettingNotificationEvent
}
