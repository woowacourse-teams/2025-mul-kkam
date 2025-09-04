package com.mulkkam.data.remote.model.response.notification

import com.mulkkam.domain.model.members.NotificationAgreedInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationAgreedResponse(
    @SerialName("isNightNotificationAgreed")
    val isNightNotificationAgreed: Boolean,
    @SerialName("isMarketingNotificationAgreed")
    val isMarketingNotificationAgreed: Boolean,
)

fun NotificationAgreedResponse.toDomain() =
    NotificationAgreedInfo(
        isNightNotificationAgreed = isNightNotificationAgreed,
        isMarketingNotificationAgreed = isMarketingNotificationAgreed,
    )
