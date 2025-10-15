package com.mulkkam.data.remote.model.response.auth

import com.mulkkam.domain.model.auth.AuthTokenInfo
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
    AuthTokenInfo(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
