package com.mulkkam.data.remote.model.response.cups

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CupsRankResponse(
    @SerialName("cups")
    val cups: List<CupRankResponse>,
)
