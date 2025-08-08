package com.mulkkam.data.remote.model.response

import com.mulkkam.data.remote.model.response.NotificationsResponse.ReadNotificationResponse
import com.mulkkam.domain.Alarm
import com.mulkkam.domain.model.Notification
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class NotificationsResponse(
    @SerialName("readNotificationResponses")
    val readNotificationResponses: List<ReadNotificationResponse>,
    @SerialName("nextCursor")
    val nextCursor: Int?,
) {
    @Serializable
    data class ReadNotificationResponse(
        @SerialName("id")
        val id: Int,
        @SerialName("title")
        val title: String,
        @SerialName("type")
        val type: String,
        @SerialName("createdAt")
        val createdAt: String,
        @SerialName("recommendedTargetAmount")
        val recommendedTargetAmount: Int?,
        @SerialName("isRead")
        val isRead: Boolean,
    )
}

fun NotificationsResponse.toDomain(): List<Notification> = this.readNotificationResponses.map { it.toDomain() }

fun ReadNotificationResponse.toDomain(): Notification =
    Notification(
        id = id,
        title = title,
        type = Alarm.from(type),
        createdAt = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME),
        recommendedTargetAmount = recommendedTargetAmount ?: 0,
        isRead = isRead,
    )
