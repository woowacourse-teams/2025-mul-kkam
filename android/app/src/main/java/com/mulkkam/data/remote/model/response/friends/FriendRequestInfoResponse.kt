package com.mulkkam.data.remote.model.response.friends

import com.mulkkam.domain.model.friends.FriendsRequestInfo
import com.mulkkam.domain.model.members.Nickname
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class FriendRequestInfoResponse(
    @SerialName("friendRequestId")
    val friendRequestId: Long,
    @SerialName("memberId")
    val memberId: Long,
    @SerialName("memberNickname")
    val memberNickname: String,
    @SerialName("createdAt")
    val createdAt: String? = null,
)

fun FriendRequestInfoResponse.toDomain(): FriendsRequestInfo =
    FriendsRequestInfo(
        memberId = memberId,
        nickname = Nickname(memberNickname),
        createdAt = createdAt?.let { LocalDateTime.parse(it) },
    )
