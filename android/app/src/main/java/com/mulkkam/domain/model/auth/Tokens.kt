package com.mulkkam.domain.model.auth

data class Tokens(
    val accessToken: String,
    val refreshToken: String,
)
