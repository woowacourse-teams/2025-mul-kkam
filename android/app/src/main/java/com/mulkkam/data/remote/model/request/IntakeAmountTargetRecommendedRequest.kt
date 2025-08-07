package com.mulkkam.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntakeAmountTargetRecommendedRequest(
    @SerialName("gender")
    val gender: String?,
    @SerialName("weight")
    val weight: Double?,
)
