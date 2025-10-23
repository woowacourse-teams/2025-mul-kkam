package com.mulkkam.data.remote.model.request.friends

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendWaterBalloonRequest(
    @SerialName("memberId")
    val memberId: Long,
)
