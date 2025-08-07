package com.mulkkam.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CupRankRequest(
    @SerialName("id")
    val id: Long,
    @SerialName("rank")
    val rank: Int,
)
