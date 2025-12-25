package com.mulkkam.domain.repository

import com.mulkkam.domain.model.OnboardingInfo
import com.mulkkam.domain.model.UserAuthState
import com.mulkkam.domain.model.result.MulKkamResult

interface OnboardingRepository {
    suspend fun postOnboarding(onboardingInfo: OnboardingInfo): MulKkamResult<Unit>

    suspend fun getOnboardingCheck(): MulKkamResult<UserAuthState>
}
