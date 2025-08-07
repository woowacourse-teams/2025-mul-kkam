package com.mulkkam.ui.settingcups.model

import com.mulkkam.domain.model.Cups

data class CupsUiModel(
    val cups: List<CupUiModel>,
    val isAddable: Boolean,
)

fun Cups.toUi(): CupsUiModel =
    CupsUiModel(
        cups = cups.map { cup -> cup.toUi() },
        isAddable = isMaxSize.not(),
    )

fun CupsUiModel.toDomain(): Cups =
    Cups(
        cups = cups.map { cup -> cup.toDomain() },
    )
