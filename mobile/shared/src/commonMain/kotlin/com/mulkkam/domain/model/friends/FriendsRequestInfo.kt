package com.mulkkam.domain.model.friends

import com.mulkkam.domain.model.members.Nickname
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class FriendsRequestInfo(
    val memberId: Long,
    val nickname: Nickname,
    val createdAt: LocalDateTime? = null,
)
