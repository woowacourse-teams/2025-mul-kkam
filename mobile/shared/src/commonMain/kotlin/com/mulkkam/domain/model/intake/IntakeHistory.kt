package com.mulkkam.domain.model.intake

import com.mulkkam.domain.model.IntakeType
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class IntakeHistory(
    val id: Int,
    val dateTime: LocalTime,
    val intakeAmount: Int,
    val intakeType: IntakeType,
    val cupEmojiUrl: String,
)
