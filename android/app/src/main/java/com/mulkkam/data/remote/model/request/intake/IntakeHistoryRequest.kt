package com.mulkkam.data.remote.model.request.intake

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntakeHistoryRequest(
    @SerialName("dateTime")
    val dateTime: String,
    @SerialName("intakeAmount")
    val intakeAmount: Int,
)
