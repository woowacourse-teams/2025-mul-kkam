package com.mulkkam.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MinimumVersionResponse(
    @SerialName("minimumVersion")
    val minimumVersion: String,
)
