package com.mulkkam.data.remote.model.response.cups

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CupRankResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("rank")
    val rank: Int,
)
