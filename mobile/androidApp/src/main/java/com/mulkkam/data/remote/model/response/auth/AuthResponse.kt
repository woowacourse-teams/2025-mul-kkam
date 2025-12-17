package com.mulkkam.data.remote.model.response.auth

import com.mulkkam.domain.model.auth.AuthInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String,
    @SerialName("finishedOnboarding")
    val onboardingCompleted: Boolean,
)

fun AuthResponse.toDomain() =
    AuthInfo(
        accessToken = accessToken,
        refreshToken = refreshToken,
        onboardingCompleted = onboardingCompleted,
    )
