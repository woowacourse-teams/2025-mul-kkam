package com.mulkkam.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceRequest(
    @SerialName("token")
    val token: String,
    @SerialName("deviceId")
    val deviceId: String,
)
