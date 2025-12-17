package com.mulkkam.data.remote.model.response.cups

import com.mulkkam.domain.model.cups.CupEmoji
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CupEmojiResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("cupEmojiUrl")
    val cupEmojiUrl: String,
)

fun CupEmojiResponse.toDomain(): CupEmoji =
    CupEmoji(
        id = id,
        cupEmojiUrl = cupEmojiUrl,
    )
