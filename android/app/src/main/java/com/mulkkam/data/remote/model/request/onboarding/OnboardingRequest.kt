package com.mulkkam.data.remote.model.request.onboarding

import com.mulkkam.domain.model.members.OnboardingInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OnboardingRequest(
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
    @SerialName("createCupRequests")
    val createCupRequests: List<CreateCupRequest>,
)

fun OnboardingInfo.toData(): OnboardingRequest =
    OnboardingRequest(
        memberNickname = nickname?.name ?: "",
        weight = weight?.value?.toDouble(),
        gender = gender?.name,
        targetIntakeAmount = targetAmount ?: 1600,
        isMarketingNotificationAgreed = isMarketingNotificationAgreed,
        isNightNotificationAgreed = isNightNotificationAgreed,
        createCupRequests = cups.map { it.toData() },
    )
