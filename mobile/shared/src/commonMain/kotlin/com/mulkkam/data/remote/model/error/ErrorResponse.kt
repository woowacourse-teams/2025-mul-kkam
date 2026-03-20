package com.mulkkam.data.remote.model.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("code")
    val code: String? = null,
    @SerialName("error")
    val error: String? = null,
)
