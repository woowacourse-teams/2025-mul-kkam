package com.mulkkam.ui.onboarding

import com.mulkkam.ui.onboarding.bioinfo.Gender

data class OnboardingInfo(
    val nickname: String? = null,
    val gender: Gender? = null,
    val weight: Int? = null,
    val targetAmount: Int? = null,
)
