package com.mulkkam.domain.model.friend

import kotlinx.serialization.Serializable

@Serializable
data class FriendsResult(
    val friends: List<Friend>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)
