package com.mulkkam.data.remote.model.response.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("finishedOnboarding")
    val onboardingCompleted: Boolean,
)
