package com.mulkkam.ui.setting.adapter

import com.mulkkam.ui.setting.model.SettingType

sealed class SettingItem(
    val viewType: SettingViewType,
) {
    data class TitleItem(
        val title: String,
    ) : SettingItem(SettingViewType.TITLE)

    data class NormalItem(
        val label: String,
        val type: SettingType.Normal,
    ) : SettingItem(SettingViewType.NORMAL)

    data class SwitchItem(
        val label: String,
        val isChecked: Boolean,
        val type: SettingType.Switch,
    ) : SettingItem(SettingViewType.SWITCH)

    data object DividerItem : SettingItem(SettingViewType.DIVIDER)
}
