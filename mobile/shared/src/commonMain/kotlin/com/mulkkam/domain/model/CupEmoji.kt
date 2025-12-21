package com.mulkkam.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CupEmoji(
    val id: Long,
    val cupEmojiUrl: String,
)
