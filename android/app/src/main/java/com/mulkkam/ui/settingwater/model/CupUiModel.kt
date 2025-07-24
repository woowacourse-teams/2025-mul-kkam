package com.mulkkam.ui.settingwater.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CupUiModel(
    val id: Int,
    val nickname: String,
    val amount: Int,
    val rank: Int,
    val isRepresentative: Boolean = false,
) : Parcelable {
    companion object {
        val EMPTY_CUP_UI_MODEL =
            CupUiModel(
                id = 0,
                nickname = "",
                amount = 0,
                rank = 0,
                isRepresentative = false,
            )
    }
}
