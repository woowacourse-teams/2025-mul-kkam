package com.mulkkam.data.remote.model.request.members

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarketingNotificationAgreedRequest(
    @SerialName("isMarketingNotificationAgreed")
    val isMarketingNotificationAgreed: Boolean,
)
