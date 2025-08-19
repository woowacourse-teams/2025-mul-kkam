package com.mulkkam.data.remote.model.response.notification

import com.mulkkam.domain.model.notification.Notification
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationsResponse(
    @SerialName("readNotificationResponses")
    val readNotificationResponses: List<ReadNotificationResponse>,
    @SerialName("nextCursor")
    val nextCursor: Long?,
)

fun NotificationsResponse.toDomain(): List<Notification> = this.readNotificationResponses.map { it.toDomain() }
