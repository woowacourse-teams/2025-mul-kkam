package com.mulkkam.data.remote.model.request.intake

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntakeHistoryCupRequest(
    @SerialName("dateTime")
    val dateTime: String,
    @SerialName("cupId")
    val cupId: Long,
)
