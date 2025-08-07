package com.mulkkam.data.remote.model.request

import com.mulkkam.domain.model.Cup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddCupRequest(
    @SerialName("cupAmount")
    val cupAmount: Int,
    @SerialName("cupNickname")
    val cupNickname: String,
    @SerialName("intakeType")
    val intakeType: String,
    @SerialName("emoji")
    val emoji: String,
)

fun Cup.toAddCupRequest(): AddCupRequest =
    AddCupRequest(
        cupAmount = amount,
        cupNickname = nickname,
        intakeType = intakeType.name,
        emoji = emoji,
    )
