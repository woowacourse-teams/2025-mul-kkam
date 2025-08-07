package com.mulkkam.domain.model

import com.mulkkam.domain.Gender

data class OnboardingInfo(
    val nickname: String? = null,
    val weight: Int? = null,
    val gender: Gender? = null,
    val targetAmount: Int? = null,
    val isMarketingNotificationAgreed: Boolean = false,
    val isNightNotificationAgreed: Boolean = false,
)
