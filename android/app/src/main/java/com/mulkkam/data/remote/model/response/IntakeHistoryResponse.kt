package com.mulkkam.data.remote.model.response

import com.mulkkam.domain.IntakeHistory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class IntakeHistoryResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("dateTime")
    val dateTime: String,
    @SerialName("intakeAmount")
    val intakeAmount: Int,
)

fun IntakeHistoryResponse.toDomain() =
    IntakeHistory(
        id = id,
        dateTime = LocalDateTime.parse(this.dateTime),
        intakeAmount = intakeAmount,
    )
