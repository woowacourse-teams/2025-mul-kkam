package com.mulkkam.ui.settingwater.adapter

import com.mulkkam.ui.settingwater.adapter.SettingWaterViewType.ADD
import com.mulkkam.ui.settingwater.adapter.SettingWaterViewType.CUP
import com.mulkkam.ui.settingwater.model.CupUiModel

sealed class SettingWaterItem(
    val viewType: SettingWaterViewType,
) {
    abstract val id: Int

    data class CupItem(
        val value: CupUiModel,
    ) : SettingWaterItem(CUP) {
        override val id: Int
            get() = value.id
    }

    data object AddItem : SettingWaterItem(ADD) {
        private const val LOAD_MORE_ITEM_ID = -1
        override val id: Int = LOAD_MORE_ITEM_ID
    }
}
