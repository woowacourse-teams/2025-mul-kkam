package com.mulkkam.data.remote.model.response.members

import com.mulkkam.domain.model.members.MemberSearchResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MembersSearchResponse(
    @SerialName("memberSearchItemResponses")
    val memberSearchItemResponses: List<MemberSearchItemResponse>,
    @SerialName("nextId")
    val nextId: Long?,
    @SerialName("hasNext")
    val hasNext: Boolean,
)

fun MembersSearchResponse.toDomain(): MemberSearchResult =
    MemberSearchResult(
        memberSearchInfos = memberSearchItemResponses.map { it.toDomain() },
        nextId = nextId,
    )
