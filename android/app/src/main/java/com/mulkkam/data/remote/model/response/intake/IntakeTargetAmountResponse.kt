package com.mulkkam.data.remote.model.response.intake

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntakeTargetAmountResponse(
    @SerialName("amount")
    val amount: Int,
)
