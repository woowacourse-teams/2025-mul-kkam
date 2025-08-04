package com.mulkkam.domain

import com.mulkkam.domain.IntakeHistorySummary.Companion.EMPTY_DAILY_WATER_INTAKE
import java.time.LocalDate

data class IntakeHistorySummaries(
    val intakeHistorySummaries: List<IntakeHistorySummary>,
) {
    val firstDay: LocalDate get() = intakeHistorySummaries.first().date

    val lastDay: LocalDate get() = intakeHistorySummaries.last().date

    val isCurrentYear: Boolean get() = firstDay.year == LocalDate.now().year && lastDay.year == LocalDate.now().year

    fun getByDateOrEmpty(targetDate: LocalDate): IntakeHistorySummary =
        intakeHistorySummaries.find { it.date == targetDate }
            ?: EMPTY_DAILY_WATER_INTAKE.copy(date = targetDate)

    fun getByIndex(index: Int): IntakeHistorySummary = intakeHistorySummaries.getOrNull(index) ?: EMPTY_DAILY_WATER_INTAKE

    fun getDateByWeekOffset(offset: Long): LocalDate = firstDay.plusWeeks(offset)
}
