package com.mulkkam.domain.model.friend

data class FriendsResult(
    val friends: List<Friend>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)
