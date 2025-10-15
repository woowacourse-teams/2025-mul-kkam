package com.mulkkam.data.remote.model.response.intake

import com.mulkkam.domain.model.intake.AchievementRate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadAchievementRateResponse(
    @SerialName("achievementRate")
    val achievementRate: Double,
)

fun ReadAchievementRateResponse.toDomain() =
    AchievementRate(
        achievementRate = achievementRate.toFloat(),
    )
