package com.mulkkam.data.remote.model.response.friends

import com.mulkkam.domain.model.friends.FriendsRequestResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendsRequestResponse(
    @SerialName("results")
    val results: List<FriendRequestInfoResponse>,
    @SerialName("nextId")
    val nextId: Long,
    @SerialName("hasNext")
    val hasNext: Boolean,
)

fun FriendsRequestResponse.toDomain(): FriendsRequestResult =
    FriendsRequestResult(
        friendsRequestInfos = results.map { it.toDomain() },
        nextId = nextId,
    )
