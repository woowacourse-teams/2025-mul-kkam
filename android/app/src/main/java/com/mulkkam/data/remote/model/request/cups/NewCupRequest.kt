package com.mulkkam.data.remote.model.request.cups

import com.mulkkam.domain.model.cups.Cup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewCupRequest(
    @SerialName("cupAmount")
    val cupAmount: Int,
    @SerialName("cupNickname")
    val cupNickname: String,
    @SerialName("intakeType")
    val intakeType: String,
    @SerialName("emoji")
    val emoji: String,
)

fun Cup.toNewCupRequest(): NewCupRequest =
    NewCupRequest(
        cupAmount = amount.value,
        cupNickname = name.value,
        intakeType = intakeType.name,
        emoji = emoji,
    )
