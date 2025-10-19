package com.mulkkam.data.remote.model.response.friends

import com.mulkkam.domain.model.friends.FriendsRequestResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendsRequestSentResponse(
    @SerialName("sentFriendRelationInfos")
    val results: List<FriendRequestInfoResponse>,
    @SerialName("nextId")
    val nextId: Long,
    @SerialName("hasNext")
    val hasNext: Boolean,
)

@Serializable
data class FriendsRequestReceivedResponse(
    @SerialName("friendRelationResponses")
    val results: List<FriendRequestInfoResponse>,
    @SerialName("nextId")
    val nextId: Long,
    @SerialName("hasNext")
    val hasNext: Boolean,
)

fun FriendsRequestSentResponse.toDomain(): FriendsRequestResult =
    FriendsRequestResult(
        friendsRequestInfos = results.map { it.toDomain() },
        nextId = nextId,
    )

fun FriendsRequestReceivedResponse.toDomain(): FriendsRequestResult =
    FriendsRequestResult(
        friendsRequestInfos = results.map { it.toDomain() },
        nextId = nextId,
    )
