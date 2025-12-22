package com.mulkkam.data.remote.model.response.intake

import com.mulkkam.domain.model.IntakeType
import com.mulkkam.domain.model.intake.IntakeHistory
import kotlinx.datetime.LocalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntakeHistoryResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("time")
    val dateTime: String,
    @SerialName("intakeAmount")
    val intakeAmount: Int,
    @SerialName("intakeType")
    val intakeType: String,
    @SerialName("cupEmojiUrl")
    val cupEmojiUrl: String,
)

fun IntakeHistoryResponse.toDomain(): IntakeHistory {
    // Parse "HH:mm:ss" format
    val parts = dateTime.split(":")
    val hour = parts[0].toInt()
    val minute = parts[1].toInt()
    val second = parts.getOrNull(2)?.toInt() ?: 0

    return IntakeHistory(
        id = id,
        dateTime = LocalTime(hour, minute, second),
        intakeAmount = intakeAmount,
        intakeType = IntakeType.from(intakeType),
        cupEmojiUrl = cupEmojiUrl,
    )
}
