package com.mulkkam.data.remote.model.request.friends

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatchFriendsRequest(
    @SerialName("status")
    val status: String,
)
