package com.mulkkam.domain

import java.time.LocalDateTime

data class IntakeHistory(
    val id: Int,
    val dateTime: LocalDateTime,
    val intakeAmount: Int,
)
