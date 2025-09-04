package com.mulkkam.data.remote.model.response.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationUnreadCountResponse(
    @SerialName("count")
    val count: Long,
)
