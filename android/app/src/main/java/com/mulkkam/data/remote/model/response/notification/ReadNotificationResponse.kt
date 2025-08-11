package com.mulkkam.data.remote.model.response.notification

import com.mulkkam.domain.model.Notification
import com.mulkkam.domain.model.NotificationType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

fun ReadNotificationResponse.toDomain(): Notification =
    Notification(
        id = id,
        title = title,
        type = NotificationType.from(type),
        createdAt = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME),
        recommendedTargetAmount = recommendedTargetAmount ?: 0,
        isRead = isRead,
    )
