package com.mulkkam.ui.setting.accountinfo.model

sealed interface SettingAccountInfoEvent {
    data object LogoutSuccess : SettingAccountInfoEvent

    data object DeleteSuccess : SettingAccountInfoEvent
}
