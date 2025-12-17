package com.mulkkam.ui.settingcups.adapter

import com.mulkkam.ui.settingcups.adapter.SettingCupsViewType.ADD
import com.mulkkam.ui.settingcups.adapter.SettingCupsViewType.CUP
import com.mulkkam.ui.settingcups.model.CupUiModel

sealed class SettingCupsItem(
    val viewType: SettingCupsViewType,
) {
    abstract val rank: Int

    data class CupItem(
        val value: CupUiModel,
    ) : SettingCupsItem(CUP) {
        override val rank: Int
            get() = value.rank
    }

    data object AddItem : SettingCupsItem(ADD) {
        private const val LOAD_MORE_ITEM_ID: Int = -1
        override val rank: Int = LOAD_MORE_ITEM_ID
    }
}
