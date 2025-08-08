package com.mulkkam.domain.model

import java.time.LocalDate

data class IntakeHistorySummaries(
    val intakeHistorySummaries: List<IntakeHistorySummary>,
) {
    val firstDay: LocalDate get() = intakeHistorySummaries.first().date

    val lastDay: LocalDate get() = intakeHistorySummaries.last().date

    val isCurrentYear: Boolean get() = firstDay.year == LocalDate.now().year && lastDay.year == LocalDate.now().year

    fun getByDateOrEmpty(targetDate: LocalDate): IntakeHistorySummary =
        intakeHistorySummaries.find { it.date == targetDate }
            ?: IntakeHistorySummary.Companion.EMPTY_DAILY_WATER_INTAKE.copy(date = targetDate)

    fun getByIndex(index: Int): IntakeHistorySummary =
        intakeHistorySummaries.getOrNull(index) ?: IntakeHistorySummary.Companion.EMPTY_DAILY_WATER_INTAKE

    fun getDateByWeekOffset(offset: Long): LocalDate = firstDay.plusWeeks(offset)
}
