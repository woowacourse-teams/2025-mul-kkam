package com.mulkkam.data.remote.model.request.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthReissueRequest(
    @SerialName("refreshToken")
    val refreshToken: String,
    @SerialName("deviceUuid")
    val deviceUuid: String,
)
