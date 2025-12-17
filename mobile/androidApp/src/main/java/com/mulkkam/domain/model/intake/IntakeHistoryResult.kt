package com.mulkkam.domain.model.intake

data class IntakeHistoryResult(
    val intakeType: IntakeType,
    val achievementRate: Float,
    val comment: String,
    val intakeAmount: Int,
)
