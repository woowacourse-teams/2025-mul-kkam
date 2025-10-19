package com.mulkkam.domain.model.friends

import com.mulkkam.domain.model.members.Nickname

data class FriendsRequestInfo(
    val requestId: Long,
    val nickname: Nickname,
)
