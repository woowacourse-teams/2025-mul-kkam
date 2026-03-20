package com.mulkkam.data.remote.model.response.notifications

import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.domain.model.notification.NotificationType
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadNotificationResponse(
    @SerialName("type")
    val type: String,
    @SerialName("id")
    val id: Long,
    @SerialName("content")
    val content: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("isRead")
    val isRead: Boolean,
    @SerialName("recommendedTargetAmount")
    val recommendedTargetAmount: Int? = null,
    @SerialName("applyRecommendAmount")
    val applyRecommendAmount: Boolean? = null,
)

fun ReadNotificationResponse.toDomain(): Notification =
    Notification(
        id = id,
        title = content,
        type = NotificationType.from(type),
        createdAt = LocalDateTime.parse(createdAt),
        recommendedTargetAmount = recommendedTargetAmount ?: 0,
        isRead = isRead,
        applyRecommendAmount = applyRecommendAmount,
    )
