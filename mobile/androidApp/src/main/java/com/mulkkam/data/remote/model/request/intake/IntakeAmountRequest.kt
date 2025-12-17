package com.mulkkam.data.remote.model.request.intake

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntakeAmountRequest(
    @SerialName("amount")
    val amount: Int,
)
