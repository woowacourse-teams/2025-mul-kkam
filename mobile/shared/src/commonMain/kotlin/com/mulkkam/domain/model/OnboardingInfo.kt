package com.mulkkam.domain.model

import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.members.Nickname
import kotlinx.serialization.Serializable

@Serializable
data class OnboardingInfo(
    val nickname: Nickname? = null,
    val weight: BioWeight? = null,
    val gender: Gender? = null,
    val targetAmount: Int? = null,
    val isMarketingNotificationAgreed: Boolean = false,
    val isNightNotificationAgreed: Boolean = false,
    val cups: List<Cup> = emptyList(),
) {
    fun hasBioInfo(): Boolean = weight != null && gender != null
}
