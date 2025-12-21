package com.mulkkam.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class OnboardingInfo(
    val nickname: String? = null,
    val weight: Int? = null,
    val gender: Gender? = null,
    val targetAmount: Int? = null,
    val isMarketingNotificationAgreed: Boolean = false,
    val isNightNotificationAgreed: Boolean = false,
    val cups: List<Cup> = emptyList(),
) {
    fun hasBioInfo(): Boolean = weight != null && gender != null
}
