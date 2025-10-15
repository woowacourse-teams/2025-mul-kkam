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
    @SerialName("cupEmojiId")
    val cupEmojiId: Long,
    @SerialName("cupRank")
    val cupRank: Int? = null,
)

fun Cup.toData(): NewCupRequest =
    NewCupRequest(
        cupNickname = name.value,
        cupAmount = amount.value,
        cupRank = rank,
        intakeType = intakeType.name,
        cupEmojiId = emoji.id,
    )
