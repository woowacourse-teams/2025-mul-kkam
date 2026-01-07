package com.mulkkam.data.remote.model.response.friends

import com.mulkkam.domain.model.friend.FriendsResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendsResponse(
    @SerialName("informationOfMembers")
    val informationOfMembers: List<FriendResponse>,
    @SerialName("nextId")
    val nextId: Long?,
    @SerialName("hasNext")
    val hasNext: Boolean,
)

fun FriendsResponse.toDomain(): FriendsResult =
    FriendsResult(
        friends = informationOfMembers.map { it.toDomain() },
        nextCursor = nextId,
        hasNext = hasNext,
    )
