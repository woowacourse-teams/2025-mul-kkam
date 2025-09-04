package com.mulkkam.ui.settingtargetamount.model

import com.mulkkam.domain.model.intake.TargetAmount

data class TargetAmountUiModel(
    val nickname: String,
    val recommendedTargetAmount: TargetAmount,
    val previousTargetAmount: TargetAmount,
)
