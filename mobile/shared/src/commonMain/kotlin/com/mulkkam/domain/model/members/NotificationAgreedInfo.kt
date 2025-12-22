package com.mulkkam.domain.model.members

import kotlinx.serialization.Serializable

@Serializable
data class NotificationAgreedInfo(
    val isMarketingNotificationAgreed: Boolean,
    val isNightNotificationAgreed: Boolean,
)
