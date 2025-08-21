package com.mulkkam.fixture

import com.mulkkam.domain.model.intake.IntakeHistory
import com.mulkkam.domain.model.intake.IntakeHistorySummaries
import com.mulkkam.domain.model.intake.IntakeHistorySummary
import com.mulkkam.domain.model.intake.IntakeType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

fun getWeeklyIntakeHistories(referenceDate: LocalDate): IntakeHistorySummaries =
    IntakeHistorySummaries(
        List(WEEK_LENGTH) {
            FULL_INTAKE_HISTORY.copy(
                date =
                    referenceDate
                        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                        .plusDays(
                            it.toLong(),
                        ),
            )
        },
    )

val FULL_INTAKE_HISTORY =
    IntakeHistorySummary(
        date = LocalDate.now(),
        totalIntakeAmount = 1200,
        targetAmount = 1200,
        achievementRate = 100f,
        intakeHistories =
            listOf(
                IntakeHistory(
                    id = 1,
                    dateTime = LocalTime.of(10, 0),
                    intakeAmount = 300,
                    intakeType = IntakeType.WATER,
                    cupEmojiUrl = "",
                ),
                IntakeHistory(
                    id = 2,
                    dateTime = LocalTime.of(11, 0),
                    intakeAmount = 400,
                    intakeType = IntakeType.WATER,
                    cupEmojiUrl = "",
                ),
                IntakeHistory(
                    id = 3,
                    dateTime = LocalTime.of(12, 0),
                    intakeAmount = 500,
                    intakeType = IntakeType.WATER,
                    cupEmojiUrl = "",
                ),
            ),
    )

val HALF_INTAKE_HISTORY =
    IntakeHistorySummary(
        date = LocalDate.now(),
        totalIntakeAmount = 600,
        targetAmount = 1200,
        achievementRate = 50f,
        intakeHistories =
            listOf(
                IntakeHistory(
                    id = 1,
                    dateTime = LocalTime.of(10, 0),
                    intakeAmount = 300,
                ),
                IntakeHistory(
                    id = 2,
                    dateTime = LocalTime.of(11, 0),
                    intakeAmount = 300,
                ),
            ),
    )

val ZERO_INTAKE_HISTORY =
    IntakeHistorySummary(
        date = LocalDate.now(),
        totalIntakeAmount = 0,
        targetAmount = 1200,
        achievementRate = 0f,
        intakeHistories = emptyList(),
    )

val SAMPLE_INTAKE_HISTORY =
    IntakeHistory(
        id = 1,
        dateTime = LocalTime.of(10, 0),
        intakeAmount = 300,
    )

private const val WEEK_LENGTH: Int = 7
