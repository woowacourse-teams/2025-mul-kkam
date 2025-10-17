package com.mulkkam.domain.model.members

data class MemberSearchResult(
    val memberSearchInfos: List<MemberSearchInfo>,
    val nextId: Long?,
)
