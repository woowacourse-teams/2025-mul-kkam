package com.mulkkam.domain.model.friend

import kotlinx.serialization.Serializable

@Serializable
data class Friend(
    val id: Long,
    val nickname: String,
)
