package com.mulkkam.data.remote.model.response.cups

import com.mulkkam.domain.model.cups.Cup
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.cups.CupName
import com.mulkkam.domain.model.intake.IntakeType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CupDefaultResponse(
    @SerialName("cupNickname")
    val cupNickname: String,
    @SerialName("cupAmount")
    val cupAmount: Int,
    @SerialName("cupRank")
    val cupRank: Int,
    @SerialName("intakeType")
    val intakeType: String,
    @SerialName("emoji")
    val emoji: CupEmojiResponse,
)

fun CupDefaultResponse.toDomain() =
    Cup(
        id = 0L,
        name = CupName(cupNickname),
        amount = CupAmount(cupAmount),
        rank = cupRank,
        intakeType = IntakeType.from(intakeType),
        emoji = emoji.toDomain(),
    )
