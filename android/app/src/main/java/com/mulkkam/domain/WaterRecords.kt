package com.mulkkam.domain

import java.time.LocalDate

data class WaterRecords(
    val date: LocalDate,
    val waterRecords: List<WaterRecord>,
)
