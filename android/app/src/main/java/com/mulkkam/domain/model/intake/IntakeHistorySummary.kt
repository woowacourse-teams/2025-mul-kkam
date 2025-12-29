package com.mulkkam.domain.model.intake

import java.time.LocalDate

data class IntakeHistorySummary(
    val date: LocalDate,
    val targetAmount: Int,
    val totalIntakeAmount: Int,
    val achievementRate: Float,
    val intakeHistories: List<IntakeHistory>,
) {
    fun dayOfWeekIndex(): Int = date.dayOfWeek.value + DAY_OF_WEEK_OFFSET

    fun determineWaterIntakeState(today: LocalDate): WaterIntakeState {
        val isToday = date == today
        val isPast = date < today
        val isFull = totalIntakeAmount == targetAmount && totalIntakeAmount != INTAKE_AMOUNT_EMPTY
        val isEmpty = totalIntakeAmount == INTAKE_AMOUNT_EMPTY

        return when {
            isToday && isFull -> WaterIntakeState.Present.Full
            isToday -> WaterIntakeState.Present.NotFull
            isPast && isFull -> WaterIntakeState.Past.Full
            isPast && isEmpty -> WaterIntakeState.Past.NoRecord
            isPast -> WaterIntakeState.Past.Partial
            else -> WaterIntakeState.Future
        }
    }

    fun afterDeleteHistory(history: IntakeHistory): IntakeHistorySummary {
        val updatedHistories = this.intakeHistories.filter { it.id != history.id }
        val newTotalAmount = updatedHistories.sumOf { it.intakeAmount }
        val newAchievementRate =
            if (this.targetAmount > INTAKE_AMOUNT_EMPTY) {
                (newTotalAmount.toFloat() / this.targetAmount * ACHIEVEMENT_RATE_MAX).coerceAtMost(ACHIEVEMENT_RATE_MAX)
            } else {
                ZERO_FLOAT
            }

        return this.copy(
            intakeHistories = updatedHistories,
            totalIntakeAmount = newTotalAmount,
            achievementRate = newAchievementRate,
        )
    }

    companion object {
        private const val INTAKE_AMOUNT_EMPTY: Int = 0
        private const val DAY_OF_WEEK_OFFSET: Int = -1
        const val ACHIEVEMENT_RATE_MAX: Float = 100f
        private const val ZERO_FLOAT: Float = 0f

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
