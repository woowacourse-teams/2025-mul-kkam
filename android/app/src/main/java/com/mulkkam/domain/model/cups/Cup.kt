package com.mulkkam.domain.model.cups

import com.mulkkam.domain.model.intake.IntakeType

data class Cup(
    val id: Long,
    val nickname: String,
    val amount: Int,
    val rank: Int,
    val intakeType: IntakeType,
    val emoji: String,
)
