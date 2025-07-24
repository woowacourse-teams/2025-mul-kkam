package com.mulkkam.ui.settingwater.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CupUiModel(
    val id: Int,
    val nickname: String,
    val cupAmount: Int,
    val cupRank: Int,
    val isRepresentative: Boolean = false,
) : Parcelable
