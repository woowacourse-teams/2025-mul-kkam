package com.mulkkam.ui.setting.model

sealed class SettingItem {
    data class TitleItem(
        val title: String,
    ) : SettingItem()

    data class NormalItem(
        val label: String,
        val type: SettingType,
    ) : SettingItem()

    data object DividerItem : SettingItem()
}
