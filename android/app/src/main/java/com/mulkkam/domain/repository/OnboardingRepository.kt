package com.mulkkam.domain.repository

import com.mulkkam.domain.model.members.OnboardingInfo
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.ui.model.UserAuthState

interface OnboardingRepository {
    suspend fun postOnboarding(onboardingInfo: OnboardingInfo): MulKkamResult<Unit>

    suspend fun getOnboardingCheck(): MulKkamResult<UserAuthState>
}
