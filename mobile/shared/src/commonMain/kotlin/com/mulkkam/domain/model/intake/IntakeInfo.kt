package com.mulkkam.domain.model.intake

import com.mulkkam.domain.model.IntakeType
import kotlinx.serialization.Serializable

@Serializable
data class IntakeInfo(
    val intakeType: IntakeType,
    val amount: Int,
)
