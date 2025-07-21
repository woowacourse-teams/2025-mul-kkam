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
        fun getEmptyIntake(date: LocalDate) =
            DailyWaterIntake(
                id = 0,
                date = date,
                targetAmount = 0,
                intakeAmount = 0,
                goalRate = 0f,
            )
    }
}
