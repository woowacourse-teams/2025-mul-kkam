package com.mulkkam.domain.model.notification

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: Long,
    val title: String,
    val type: NotificationType,
    val createdAt: LocalDateTime,
    val recommendedTargetAmount: Int?,
    val isRead: Boolean,
    val applyRecommendAmount: Boolean?,
)
