package com.mulkkam.domain.model.cups

import kotlinx.serialization.Serializable

@Serializable
data class CupEmoji(
    val id: Int,
    val cupEmojiUrl: String,
)
