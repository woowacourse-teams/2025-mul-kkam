package com.mulkkam.domain.model.intake

import java.time.LocalTime

data class IntakeHistory(
    val id: Int,
    val dateTime: LocalTime,
    val intakeAmount: Int,
)
