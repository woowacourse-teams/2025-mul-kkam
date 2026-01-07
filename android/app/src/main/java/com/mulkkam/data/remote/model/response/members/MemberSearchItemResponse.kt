package com.mulkkam.data.remote.model.response.members

import com.mulkkam.domain.model.friends.FriendRequestStatus
import com.mulkkam.domain.model.members.MemberSearchInfo
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.domain.model.members.RequestDirection
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberSearchItemResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("memberNickname")
    val memberNickname: String,
    @SerialName("status")
    val status: String,
    @SerialName("direction")
    val direction: String,
)

fun MemberSearchItemResponse.toDomain(): MemberSearchInfo =
    MemberSearchInfo(
        id = id,
        nickname = Nickname(memberNickname),
        status = FriendRequestStatus.from(status),
        direction = RequestDirection.from(direction),
    )
