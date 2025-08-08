package com.mulkkam.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhysicalAttributes(
    @SerialName("gender")
    val gender: String,
    @SerialName("weight")
    val weight: Double,
)
