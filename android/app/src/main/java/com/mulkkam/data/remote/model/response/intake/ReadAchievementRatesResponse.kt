package com.mulkkam.data.remote.model.response.intake

import com.mulkkam.domain.model.intake.AchievementRate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadAchievementRatesResponse(
    @SerialName("readAchievementRateByDateResponses")
    val achievementRates: List<ReadAchievementRateResponse>,
)

fun ReadAchievementRatesResponse.toDomain(): List<AchievementRate> = achievementRates.map { it.toDomain() }
