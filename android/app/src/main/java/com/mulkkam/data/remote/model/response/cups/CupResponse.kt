package com.mulkkam.data.remote.model.response.cups

import com.mulkkam.domain.model.cups.Cup
import com.mulkkam.domain.model.cups.CupCapacity
import com.mulkkam.domain.model.cups.CupName
import com.mulkkam.domain.model.intake.IntakeType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CupResponse(
    @SerialName("id")
    val id: Long,
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
        nickname = CupName(cupNickname),
        amount = CupCapacity(cupAmount),
        rank = cupRank,
        intakeType = IntakeType.from(intakeType),
        emoji = emoji,
    )
