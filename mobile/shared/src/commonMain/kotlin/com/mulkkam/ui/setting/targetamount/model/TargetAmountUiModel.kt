package com.mulkkam.ui.setting.targetamount.model

import com.mulkkam.domain.model.intake.TargetAmount

data class TargetAmountUiModel(
    val nickname: String,
    val recommendedTargetAmount: TargetAmount,
    val previousTargetAmount: TargetAmount,
)
