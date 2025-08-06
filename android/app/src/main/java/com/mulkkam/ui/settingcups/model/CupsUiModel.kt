package com.mulkkam.ui.settingcups.model

import com.mulkkam.domain.Cups

data class CupsUiModel(
    val cups: List<CupUiModel>,
    val isAddable: Boolean,
)

fun Cups.toUi(): CupsUiModel =
    CupsUiModel(
        cups = cups.map { cup -> cup.toUi() },
        isAddable = isMaxSize.not(),
    )
