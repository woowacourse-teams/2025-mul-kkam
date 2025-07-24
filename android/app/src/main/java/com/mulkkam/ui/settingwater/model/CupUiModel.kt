package com.mulkkam.ui.settingwater.model

data class CupUiModel(
    val id: Int,
    val nickname: String,
    val cupAmount: Int,
    val cupRank: Int,
    val isRepresentative: Boolean = false,
)
