package com.mulkkam.data.remote.model.response.cups

import com.mulkkam.domain.model.cups.Cups
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CupsDefaultResponse(
    @SerialName("cups")
    val cups: List<CupDefaultResponse>,
)

fun CupsDefaultResponse.toDomain() =
    Cups(
        cups = cups.map { it.toDomain() },
    )
