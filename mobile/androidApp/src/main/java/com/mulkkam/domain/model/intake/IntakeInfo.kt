package com.mulkkam.domain.model.intake

import com.mulkkam.domain.model.IntakeType

data class IntakeInfo(
    val intakeType: IntakeType,
    val amount: Int,
)
