package com.mulkkam.data.remote.model.response.intake

import com.mulkkam.domain.model.intake.IntakeHistoryResult
import com.mulkkam.domain.model.intake.IntakeType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntakeHistoryResultResponse(
    @SerialName("achievementRate")
    val achievementRate: Float,
    @SerialName("comment")
    val comment: String,
    @SerialName("intakeAmount")
    val intakeAmount: Int,
    @SerialName("intakeType")
    val intakeType: String,
)

fun IntakeHistoryResultResponse.toDomain() =
    IntakeHistoryResult(
        achievementRate = achievementRate,
        comment = comment,
        intakeAmount = intakeAmount,
        intakeType = IntakeType.from(intakeType),
    )
