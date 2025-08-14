package com.mulkkam.data.remote.model.request.cups

import com.mulkkam.domain.model.cups.Cup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatchCupRequest(
    @SerialName("cupNickname")
    val cupNickname: String,
    @SerialName("cupAmount")
    val cupAmount: Int,
    @SerialName("intakeType")
    val intakeType: String,
    @SerialName("emoji")
    val emoji: String,
)

fun Cup.toPatchCupRequest(): PatchCupRequest =
    PatchCupRequest(
        cupNickname = nickname.value,
        cupAmount = amount.value,
        intakeType = intakeType.name,
        emoji = emoji,
    )
