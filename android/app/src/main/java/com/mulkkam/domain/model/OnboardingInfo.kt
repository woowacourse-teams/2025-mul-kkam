package com.mulkkam.domain.model

data class OnboardingInfo(
    val nickname: String? = null,
    val weight: BioWeight? = null,
    val gender: Gender? = null,
    val targetAmount: Int? = null,
    val isMarketingNotificationAgreed: Boolean = false,
    val isNightNotificationAgreed: Boolean = false,
)
