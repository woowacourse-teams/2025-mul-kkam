package com.mulkkam.domain

import java.time.LocalDate

data class DailyWaterIntake(
    val id: Long,
    val date: LocalDate,
    val targetAmount: Int,
    val intakeAmount: Int,
    val goalRate: Float,
)
