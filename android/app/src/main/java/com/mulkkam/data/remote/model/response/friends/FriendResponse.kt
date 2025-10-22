package com.mulkkam.data.remote.model.response.friends

import com.mulkkam.domain.model.friend.Friend
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendResponse(
    @SerialName("memberId")
    val memberId: Long,
    @SerialName("memberNickname")
    val memberNickname: String,
)

fun FriendResponse.toDomain(): Friend =
    Friend(
        id = memberId,
        nickname = memberNickname,
    )
