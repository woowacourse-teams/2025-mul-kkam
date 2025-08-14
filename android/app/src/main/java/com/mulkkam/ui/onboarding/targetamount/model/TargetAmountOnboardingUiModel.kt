package com.mulkkam.ui.onboarding.targetamount.model

import com.mulkkam.domain.model.intake.TargetAmount

data class TargetAmountOnboardingUiModel(
    val nickname: String,
    val recommendedTargetAmount: TargetAmount,
)
