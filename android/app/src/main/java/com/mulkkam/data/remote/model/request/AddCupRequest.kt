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
)

fun Cup.toAddCupRequest(): AddCupRequest =
    AddCupRequest(
        cupAmount = amount,
        cupNickname = nickname,
    )
