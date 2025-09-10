package com.mulkkam.data.remote.model.response.auth

import com.mulkkam.domain.model.auth.AuthTokenInfo
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
    AuthTokenInfo(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
