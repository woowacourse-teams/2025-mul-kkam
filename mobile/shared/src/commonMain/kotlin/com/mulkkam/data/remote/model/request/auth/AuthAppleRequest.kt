package com.mulkkam.data.remote.model.request.auth


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthAppleRequest(
    @SerialName("authorizationCode")
    val authorizationCode: String,
    @SerialName("deviceUuid")
    val deviceUuid: String
)
