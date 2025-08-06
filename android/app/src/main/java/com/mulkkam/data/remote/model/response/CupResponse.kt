package com.mulkkam.data.remote.model.response

import com.mulkkam.domain.model.Cup
import com.mulkkam.domain.model.IntakeType
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
    @SerialName("intakeType")
    val intakeType: String,
    @SerialName("emoji")
    val emoji: String,
)

fun CupResponse.toDomain() =
    Cup(
        id = id,
        nickname = cupNickname,
        amount = cupAmount,
        rank = cupRank,
        intakeType = IntakeType.from(intakeType),
        emoji = emoji,
    )
