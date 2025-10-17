package com.mulkkam.domain.model.members

data class MemberSearchInfo(
    val id: Long,
    val nickname: Nickname,
    val status: Status,
    val direction: Direction,
)
