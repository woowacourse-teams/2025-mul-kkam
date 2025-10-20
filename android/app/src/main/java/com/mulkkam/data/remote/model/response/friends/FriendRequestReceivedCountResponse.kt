package com.mulkkam.data.remote.model.response.friends

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestReceivedCountResponse(
    @SerialName("count")
    val count: Int,
)
