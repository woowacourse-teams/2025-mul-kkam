package com.mulkkam.ui.fixture

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
                    id = 1,
                    dateTime = LocalTime.of(11, 0),
                    intakeAmount = 400,
                    intakeType = IntakeType.WATER,
                    cupEmojiUrl = "",
                ),
                IntakeHistory(
                    id = 1,
                    dateTime = LocalTime.of(12, 0),
                    intakeAmount = 500,
                    intakeType = IntakeType.WATER,
                    cupEmojiUrl = "",
                ),
            ),
    )

private const val WEEK_LENGTH: Int = 7
