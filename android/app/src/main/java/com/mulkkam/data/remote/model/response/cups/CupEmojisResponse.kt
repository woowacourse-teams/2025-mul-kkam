package com.mulkkam.data.remote.model.response.cups

import com.mulkkam.domain.model.cups.CupEmoji
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CupEmojisResponse(
    @SerialName("cups")
    val cupEmoji: List<CupEmojiResponse>,
)

fun CupEmojisResponse.toDomain(): List<CupEmoji> = cupEmoji.map { it.toDomain() }
