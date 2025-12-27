package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.request.onboarding.OnboardingRequest
import com.mulkkam.data.remote.model.response.onboarding.OnboardingCheckResponse

interface OnboardingDataSource {
    suspend fun postOnboarding(member: OnboardingRequest): Result<Unit>

    suspend fun getOnboardingCheck(): Result<OnboardingCheckResponse>
}
