package com.mulkkam.domain.model.auth

data class AuthTokenInfo(
    val accessToken: String,
    val refreshToken: String,
)
