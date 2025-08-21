package com.mulkkam.data.remote.model.request.cups

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
)
