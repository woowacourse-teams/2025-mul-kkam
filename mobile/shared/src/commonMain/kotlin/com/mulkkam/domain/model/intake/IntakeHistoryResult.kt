package com.mulkkam.domain.model.intake

import com.mulkkam.domain.model.IntakeType
import kotlinx.serialization.Serializable

@Serializable
data class IntakeHistoryResult(
    val intakeType: IntakeType,
    val achievementRate: Float,
    val comment: String,
    val intakeAmount: Int,
)
