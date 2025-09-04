package com.mulkkam.data.remote.model.response.intake

import com.mulkkam.domain.model.intake.IntakeHistory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Serializable
data class IntakeHistoryResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("time")
    val dateTime: String,
    @SerialName("intakeAmount")
    val intakeAmount: Int,
)

fun IntakeHistoryResponse.toDomain() =
    IntakeHistory(
        id = id,
        dateTime = LocalTime.parse(this.dateTime, DateTimeFormatter.ofPattern("HH:mm:ss")),
        intakeAmount = intakeAmount,
    )
