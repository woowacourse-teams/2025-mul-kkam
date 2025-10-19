package com.mulkkam.domain.model.friends

import com.mulkkam.domain.model.members.Nickname
import java.time.LocalDateTime

data class FriendsRequestInfo(
    val requestId: Long,
    val nickname: Nickname,
    val createdAt: LocalDateTime? = null,
)
