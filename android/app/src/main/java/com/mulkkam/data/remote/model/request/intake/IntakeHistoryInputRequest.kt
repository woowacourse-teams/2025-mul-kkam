package com.mulkkam.data.remote.model.request.intake

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntakeHistoryInputRequest(
    @SerialName("dateTime")
    val dateTime: String,
    @SerialName("intakeType")
    val intakeType: String,
    @SerialName("intakeAmount")
    val intakeAmount: Int,
)
