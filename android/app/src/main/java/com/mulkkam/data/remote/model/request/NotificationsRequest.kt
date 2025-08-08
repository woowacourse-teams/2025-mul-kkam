package com.mulkkam.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationsRequest(
    @SerialName("lastId")
    val lastId: Int? = null,
    @SerialName("clientTime")
    val clientTime: String,
    @SerialName("size")
    val size: Int,
)
