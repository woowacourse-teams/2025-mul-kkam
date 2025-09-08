package com.mulkkam.data.remote.model.request.onboarding

import com.mulkkam.domain.model.cups.Cup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateCupRequest(
    @SerialName("cupNickname")
    val cupNickname: String,
    @SerialName("cupAmount")
    val cupAmount: Int,
    @SerialName("cupRank")
    val cupRank: Int,
    @SerialName("intakeType")
    val intakeType: String,
    @SerialName("cupEmojiId")
    val cupEmojiId: Long,
)

fun Cup.toData(): CreateCupRequest =
    CreateCupRequest(
        cupNickname = name.value,
        cupAmount = amount.value,
        cupRank = rank,
        intakeType = intakeType.name,
        cupEmojiId = emojiId,
    )
