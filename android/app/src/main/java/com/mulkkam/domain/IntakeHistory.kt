package com.mulkkam.domain

import java.time.LocalTime

data class IntakeHistory(
    val id: Int,
    val dateTime: LocalTime,
    val intakeAmount: Int,
)
