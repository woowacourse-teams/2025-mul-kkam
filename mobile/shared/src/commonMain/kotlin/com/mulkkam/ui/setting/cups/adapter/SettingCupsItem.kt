package com.mulkkam.ui.setting.cups.adapter

import com.mulkkam.ui.setting.cups.model.CupUiModel

// TODO: 이거 리사이클러뷰 쓰던때 있던건데 왜 아직도 있음?
sealed class SettingCupsItem(
    val viewType: SettingCupsViewType,
) {
    abstract val rank: Int

    data class CupItem(
        val value: CupUiModel,
    ) : SettingCupsItem(SettingCupsViewType.CUP) {
        override val rank: Int
            get() = value.rank
    }

    data object AddItem : SettingCupsItem(SettingCupsViewType.ADD) {
        private const val LOAD_MORE_ITEM_ID: Int = -1
        override val rank: Int = LOAD_MORE_ITEM_ID
    }
}
