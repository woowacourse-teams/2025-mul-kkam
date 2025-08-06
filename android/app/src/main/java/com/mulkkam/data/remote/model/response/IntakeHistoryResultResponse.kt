package com.mulkkam.data.remote.model.response

import com.mulkkam.domain.model.IntakeHistoryResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntakeHistoryResultResponse(
    @SerialName("achievementRate")
    val achievementRate: Float,
    @SerialName("comment")
    val comment: String,
)

fun IntakeHistoryResultResponse.toDomain() =
    IntakeHistoryResult(
        achievementRate = achievementRate,
        comment = comment,
    )
