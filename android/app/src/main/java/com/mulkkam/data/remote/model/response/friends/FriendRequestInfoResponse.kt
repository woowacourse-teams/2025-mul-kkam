package com.mulkkam.data.remote.model.response.friends

import com.mulkkam.domain.model.friends.FriendsRequestInfo
import com.mulkkam.domain.model.members.Nickname
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestInfoResponse(
    @SerialName("friendRequestId")
    val friendRequestId: Int,
    @SerialName("memberNickname")
    val memberNickname: String,
)

fun FriendRequestInfoResponse.toDomain(): FriendsRequestInfo =
    FriendsRequestInfo(
        requestId = friendRequestId.toLong(),
        nickname = Nickname(memberNickname),
    )
