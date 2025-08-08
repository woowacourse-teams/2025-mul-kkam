package com.mulkkam.domain.model

import java.time.LocalDate

data class IntakeHistorySummary(
    val date: LocalDate,
    val targetAmount: Int,
    val totalIntakeAmount: Int,
    val achievementRate: Float,
    val intakeHistories: List<IntakeHistory>,
) {
    fun dayOfWeekIndex(): Int = date.dayOfWeek.value + DAY_OF_WEEK_OFFSET

    companion object {
        private const val DAY_OF_WEEK_OFFSET: Int = -1

        val EMPTY_DAILY_WATER_INTAKE: IntakeHistorySummary =
            IntakeHistorySummary(
                date = LocalDate.now(),
                targetAmount = 0,
                totalIntakeAmount = 0,
                achievementRate = 0f,
                intakeHistories = emptyList(),
            )
    }
}
