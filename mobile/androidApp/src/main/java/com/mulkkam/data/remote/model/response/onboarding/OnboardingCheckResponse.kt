package com.mulkkam.data.remote.model.response.onboarding

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OnboardingCheckResponse(
    @SerialName("finishedOnboarding")
    val finishedOnboarding: Boolean,
)
