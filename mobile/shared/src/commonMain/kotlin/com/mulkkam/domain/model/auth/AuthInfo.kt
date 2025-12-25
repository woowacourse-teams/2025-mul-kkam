package com.mulkkam.domain.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthInfo(
    val accessToken: String,
    val refreshToken: String,
    val onboardingCompleted: Boolean = false,
)
