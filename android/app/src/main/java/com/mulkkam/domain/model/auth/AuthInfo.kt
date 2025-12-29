package com.mulkkam.domain.model.auth

data class AuthInfo(
    val accessToken: String,
    val refreshToken: String,
    val onboardingCompleted: Boolean = false,
)
