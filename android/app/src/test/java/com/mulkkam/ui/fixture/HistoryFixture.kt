package com.mulkkam.ui.fixture

import com.mulkkam.domain.model.IntakeHistory
import com.mulkkam.domain.model.IntakeHistorySummary
import java.time.LocalDate
import java.time.LocalTime

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
                ),
                IntakeHistory(
                    id = 1,
                    dateTime = LocalTime.of(11, 0),
                    intakeAmount = 400,
                ),
                IntakeHistory(
                    id = 1,
                    dateTime = LocalTime.of(12, 0),
                    intakeAmount = 500,
                ),
            ),
    )

val HALF_INTAKE_HISTORY =
    IntakeHistorySummary(
        date = LocalDate.now(),
        totalIntakeAmount = 1200,
        targetAmount = 600,
        achievementRate = 50f,
        intakeHistories =
            listOf(
                IntakeHistory(
                    id = 1,
                    dateTime = LocalTime.of(10, 0),
                    intakeAmount = 600,
                ),
            ),
    )
