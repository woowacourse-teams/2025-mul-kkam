package com.mulkkam.domain.model.intake

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.serialization.Serializable

@Serializable
data class IntakeHistorySummaries(
    val intakeHistorySummaries: List<IntakeHistorySummary>,
) {
    val firstDay: LocalDate get() = intakeHistorySummaries.first().date

    val lastDay: LocalDate get() = intakeHistorySummaries.last().date

    fun isCurrentYear(currentDate: LocalDate): Boolean = firstDay.year == currentDate.year && lastDay.year == currentDate.year

    fun getByDateOrEmpty(targetDate: LocalDate): IntakeHistorySummary =
        intakeHistorySummaries.find { it.date == targetDate }
            ?: IntakeHistorySummary.createEmpty(targetDate)

    fun getByIndex(
        index: Int,
        defaultDate: LocalDate,
    ): IntakeHistorySummary = intakeHistorySummaries.getOrNull(index) ?: IntakeHistorySummary.createEmpty(defaultDate)

    fun getDateByWeekOffset(offset: Long): LocalDate = firstDay.plus(DatePeriod(days = (offset * 7).toInt()))
}
