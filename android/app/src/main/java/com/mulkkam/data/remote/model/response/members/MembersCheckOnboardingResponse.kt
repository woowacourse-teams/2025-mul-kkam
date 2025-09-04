package com.mulkkam.data.remote.model.response.members

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MembersCheckOnboardingResponse(
    @SerialName("finishedOnboarding")
    val finishedOnboarding: Boolean,
)
