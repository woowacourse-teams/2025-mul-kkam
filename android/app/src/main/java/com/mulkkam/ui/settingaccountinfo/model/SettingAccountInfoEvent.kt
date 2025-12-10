package com.mulkkam.ui.settingaccountinfo.model

sealed interface SettingAccountInfoEvent {
    data object LogoutSuccess : SettingAccountInfoEvent

    data object DeleteSuccess : SettingAccountInfoEvent
}
