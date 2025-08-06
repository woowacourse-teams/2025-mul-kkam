package com.mulkkam.data.remote.model.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MembersPhysicalAtrributesRequest(
    @SerialName("gender")
    val gender: String,
    @SerialName("weight")
    val weight: Double
)
