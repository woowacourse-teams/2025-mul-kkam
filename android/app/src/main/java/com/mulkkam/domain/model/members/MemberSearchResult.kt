package com.mulkkam.domain.model.members

data class MemberSearchResult(
    val memberSearchInfo: List<MemberSearchInfo>,
    val nextId: Long?,
)
