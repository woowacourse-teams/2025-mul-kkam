package com.mulkkam.data.remote.model.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationsActivityResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("memberNickname")
    val memberNickname: MemberNickname,
    @SerialName("physicalAttributes")
    val physicalAttributes: PhysicalAttributes,
    @SerialName("targetAmount")
    val targetAmount: TargetAmount,
    @SerialName("nightNotificationAgreed")
    val nightNotificationAgreed: Boolean,
    @SerialName("marketingNotificationAgreed")
    val marketingNotificationAgreed: Boolean
)
