package com.mulkkam.data.remote.model.request.friends

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatchFriendRequest(
    @SerialName("status")
    val status: String,
)
