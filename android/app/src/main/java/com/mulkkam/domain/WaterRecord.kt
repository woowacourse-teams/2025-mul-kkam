package com.mulkkam.domain

import java.time.LocalTime

data class WaterRecord(
    val id: Long,
    val time: LocalTime,
    val intakeAmount: Int,
)
