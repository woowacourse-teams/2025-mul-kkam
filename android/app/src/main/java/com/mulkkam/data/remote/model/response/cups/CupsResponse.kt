package com.mulkkam.data.remote.model.response.cups

import com.mulkkam.domain.model.Cups
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CupsResponse(
    @SerialName("size")
    val size: Int,
    @SerialName("cups")
    val cups: List<CupResponse>,
)

fun CupsResponse.toDomain() =
    Cups(
        cups = cups.map { it.toDomain() },
    )
