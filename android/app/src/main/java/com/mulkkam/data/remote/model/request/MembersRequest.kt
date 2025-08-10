package com.mulkkam.data.remote.model.request

import com.mulkkam.domain.model.OnboardingInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MembersRequest(
    @SerialName("memberNickname")
    val memberNickname: String,
    @SerialName("weight")
    val weight: Double?,
    @SerialName("gender")
    val gender: String?,
    @SerialName("targetIntakeAmount")
    val targetIntakeAmount: Int,
    @SerialName("isMarketingNotificationAgreed")
    val isMarketingNotificationAgreed: Boolean,
    @SerialName("isNightNotificationAgreed")
    val isNightNotificationAgreed: Boolean,
)

fun OnboardingInfo.toData(): MembersRequest =
    MembersRequest(
        memberNickname = nickname ?: "",
        weight = weight?.value?.toDouble(),
        gender = gender?.name,
        targetIntakeAmount = targetAmount ?: 1600,
        isMarketingNotificationAgreed = isMarketingNotificationAgreed,
        isNightNotificationAgreed = isNightNotificationAgreed,
    )
