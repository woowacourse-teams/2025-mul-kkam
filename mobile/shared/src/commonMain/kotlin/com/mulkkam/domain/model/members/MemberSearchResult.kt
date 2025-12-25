package com.mulkkam.domain.model.members

import kotlinx.serialization.Serializable

@Serializable
data class MemberSearchResult(
    val memberSearchInfos: List<MemberSearchInfo>,
    val nextId: Long?,
)
