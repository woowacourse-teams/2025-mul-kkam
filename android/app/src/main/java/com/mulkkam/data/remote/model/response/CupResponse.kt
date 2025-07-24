package com.mulkkam.data.remote.model.response

import com.mulkkam.domain.Cup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CupResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("cupNickname")
    val cupNickname: String,
    @SerialName("cupAmount")
    val cupAmount: Int,
    @SerialName("cupRank")
    val cupRank: Int,
)

fun CupResponse.toDomain() =
    Cup(
        id = id,
        cupNickname = cupNickname,
        cupAmount = cupAmount,
        cupRank = cupRank,
    )
