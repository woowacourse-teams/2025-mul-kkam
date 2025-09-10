package com.mulkkam.data.remote.model.response.intake

import com.mulkkam.domain.model.intake.IntakeHistorySummary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class IntakeHistorySummaryResponse(
    @SerialName("date")
    val date: String,
    @SerialName("targetAmount")
    val targetAmount: Int,
    @SerialName("totalIntakeAmount")
    val totalIntakeAmount: Int,
    @SerialName("achievementRate")
    val achievementRate: Double,
    @SerialName("streak")
    val streak: Int,
    @SerialName("intakeDetails")
    val intakeHistories: List<IntakeHistoryResponse>,
)

fun IntakeHistorySummaryResponse.toDomain() =
    IntakeHistorySummary(
        date = LocalDate.parse(date),
        targetAmount = targetAmount,
        totalIntakeAmount = totalIntakeAmount,
        achievementRate = achievementRate.toFloat(),
        intakeHistories = intakeHistories.map { it.toDomain() },
    )
