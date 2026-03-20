package com.mulkkam.fixture

import com.mulkkam.domain.model.IntakeType
import com.mulkkam.domain.model.intake.IntakeHistory
import com.mulkkam.domain.model.intake.IntakeHistorySummaries
import com.mulkkam.domain.model.intake.IntakeHistorySummary
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

fun getWeeklyIntakeHistories(referenceDate: LocalDate): IntakeHistorySummaries {
    val daysFromMonday = (referenceDate.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal + WEEK_LENGTH) % WEEK_LENGTH
    val monday = referenceDate.minus(daysFromMonday, DateTimeUnit.DAY)

    return IntakeHistorySummaries(
        List(WEEK_LENGTH) {
            FULL_INTAKE_HISTORY.copy(
                date = monday.plus(it, DateTimeUnit.DAY),
            )
        },
    )
}

val FULL_INTAKE_HISTORY =
    IntakeHistorySummary(
        date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        totalIntakeAmount = 1200,
        targetAmount = 1200,
        achievementRate = 100f,
        intakeHistories =
            listOf(
                IntakeHistory(
                    id = 1,
                    dateTime = LocalTime(10, 0),
                    intakeAmount = 300,
                    intakeType = IntakeType.WATER,
                    cupEmojiUrl = "",
                ),
                IntakeHistory(
                    id = 2,
                    dateTime = LocalTime(11, 0),
                    intakeAmount = 400,
                    intakeType = IntakeType.WATER,
                    cupEmojiUrl = "",
                ),
                IntakeHistory(
                    id = 3,
                    dateTime = LocalTime(12, 0),
                    intakeAmount = 500,
                    intakeType = IntakeType.WATER,
                    cupEmojiUrl = "",
                ),
            ),
    )

val HALF_INTAKE_HISTORY =
    IntakeHistorySummary(
        date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        totalIntakeAmount = 600,
        targetAmount = 1200,
        achievementRate = 50f,
        intakeHistories =
            listOf(
                IntakeHistory(
                    id = 1,
                    dateTime = LocalTime(10, 0),
                    intakeAmount = 300,
                    intakeType = IntakeType.WATER,
                    cupEmojiUrl = "",
                ),
                IntakeHistory(
                    id = 2,
                    dateTime = LocalTime(11, 0),
                    intakeAmount = 300,
                    intakeType = IntakeType.WATER,
                    cupEmojiUrl = "",
                ),
            ),
    )

val ZERO_INTAKE_HISTORY =
    IntakeHistorySummary(
        date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        totalIntakeAmount = 0,
        targetAmount = 1200,
        achievementRate = 0f,
        intakeHistories = emptyList(),
    )

val SAMPLE_INTAKE_HISTORY =
    IntakeHistory(
        id = 1,
        dateTime = LocalTime(10, 0),
        intakeAmount = 300,
        intakeType = IntakeType.WATER,
        cupEmojiUrl = "",
    )

private const val WEEK_LENGTH: Int = 7
