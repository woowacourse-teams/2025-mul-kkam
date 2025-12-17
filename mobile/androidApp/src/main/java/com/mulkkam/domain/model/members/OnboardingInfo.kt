package com.mulkkam.domain.model.members

import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.bio.Gender
import com.mulkkam.domain.model.cups.Cup
import java.io.Serializable

data class OnboardingInfo(
    val nickname: Nickname? = null,
    val weight: BioWeight? = null,
    val gender: Gender? = null,
    val targetAmount: Int? = null,
    val isMarketingNotificationAgreed: Boolean = false,
    val isNightNotificationAgreed: Boolean = false,
    val cups: List<Cup> = emptyList(),
) : Serializable {
    fun hasBioInfo(): Boolean = weight != null && gender != null
}
