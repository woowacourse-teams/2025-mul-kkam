package com.mulkkam.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntakeTargetAmountResponse(
    @SerialName("amount")
    val amount: Int,
)
