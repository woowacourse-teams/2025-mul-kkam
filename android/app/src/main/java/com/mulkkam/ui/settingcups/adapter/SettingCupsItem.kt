package com.mulkkam.ui.settingcups.adapter

import com.mulkkam.ui.settingcups.adapter.SettingCupsViewType.ADD
import com.mulkkam.ui.settingcups.adapter.SettingCupsViewType.CUP
import com.mulkkam.ui.settingcups.model.CupUiModel

sealed class SettingCupsItem(
    val viewType: SettingCupsViewType,
) {
    abstract val id: Long

    data class CupItem(
        val value: CupUiModel,
    ) : SettingCupsItem(CUP) {
        override val id: Long
            get() = value.id
    }

    data object AddItem : SettingCupsItem(ADD) {
        private const val LOAD_MORE_ITEM_ID: Long = -1L
        override val id: Long = LOAD_MORE_ITEM_ID
    }
}
