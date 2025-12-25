package com.mulkkam.domain.model.notification

import kotlinx.serialization.Serializable

@Serializable
data class NotificationsResult(
    val notifications: List<Notification>,
    val nextCursor: Long?,
)
