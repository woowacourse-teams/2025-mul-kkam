package com.mulkkam.data.remote.model.response.auth

import com.mulkkam.domain.model.auth.Tokens
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AuthReissueResponse(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String,
)

fun AuthReissueResponse.toDomain() =
    Tokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
