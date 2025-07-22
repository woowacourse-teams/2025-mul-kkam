package com.mulkkam.domain

import java.time.LocalDate

data class DailyWaterIntake(
    val id: Long,
    val date: LocalDate,
    val targetAmount: Int,
    val intakeAmount: Int,
    val goalRate: Float,
) {
    companion object {
        val EMPTY_DAILY_WATER_INTAKE: DailyWaterIntake =
            DailyWaterIntake(
                id = 0,
                date = LocalDate.now(),
                targetAmount = 0,
                intakeAmount = 0,
                goalRate = 0f,
            )
    }
}
