package com.mulkkam.data.remote.model.request.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    @SerialName("oauthAccessToken")
    val oauthAccessToken: String,
    @SerialName("deviceUuid")
    val deviceUuid: String,
)
