package com.mulkkam.data.remote.model.request

import com.mulkkam.domain.model.Cups
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CupsRankRequest(
    @SerialName("cups")
    val cups: List<CupRankRequest>,
)

fun Cups.toData(): CupsRankRequest =
    CupsRankRequest(
        cups =
            cups.map {
                CupRankRequest(id = it.id, rank = it.rank)
            },
    )
