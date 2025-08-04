package com.mulkkam.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("onboardingCompleted")
    val onboardingCompleted: Boolean,
)
