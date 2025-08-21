package com.mulkkam.domain.model.intake

import java.time.LocalDate

data class IntakeHistorySummaries(
    val intakeHistorySummaries: List<IntakeHistorySummary>,
) {
    val firstDay: LocalDate get() = intakeHistorySummaries.first().date

    val lastDay: LocalDate get() = intakeHistorySummaries.last().date

    fun isCurrentYear(currentDate: LocalDate = LocalDate.now()): Boolean =
        firstDay.year == currentDate.year && lastDay.year == currentDate.year

    fun getByDateOrEmpty(targetDate: LocalDate): IntakeHistorySummary =
        intakeHistorySummaries.find { it.date == targetDate }
            ?: IntakeHistorySummary.EMPTY_DAILY_WATER_INTAKE.copy(date = targetDate)

    fun getByIndex(index: Int): IntakeHistorySummary =
        intakeHistorySummaries.getOrNull(index) ?: IntakeHistorySummary.EMPTY_DAILY_WATER_INTAKE

    fun getDateByWeekOffset(offset: Long): LocalDate = firstDay.plusWeeks(offset)
}
