package com.mulkkam.data.remote.model.request.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActiveCaloriesBurnedRequest(
    @SerialName("burnCalorie")
    val burnCalorie: Double,
)
