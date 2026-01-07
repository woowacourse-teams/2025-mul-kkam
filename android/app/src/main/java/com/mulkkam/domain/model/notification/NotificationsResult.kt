package com.mulkkam.domain.model.notification

data class NotificationsResult(
    val notifications: List<Notification>,
    val nextCursor: Long?,
)
