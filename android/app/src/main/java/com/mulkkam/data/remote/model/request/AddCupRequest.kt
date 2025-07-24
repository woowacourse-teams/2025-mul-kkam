package com.mulkkam.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddCupRequest(
    @SerialName("cupAmount")
    val cupAmount: Int,
    @SerialName("cupNickname")
    val cupNickname: String,
)
