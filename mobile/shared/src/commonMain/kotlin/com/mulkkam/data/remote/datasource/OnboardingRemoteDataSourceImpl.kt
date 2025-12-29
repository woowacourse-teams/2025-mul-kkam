package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.request.onboarding.OnboardingRequest
import com.mulkkam.data.remote.model.response.onboarding.OnboardingCheckResponse

// TODO: DataSource 구현 필요
// 앱 실행을 위한 임시 코드
class OnboardingRemoteDataSourceImpl : OnboardingRemoteDataSource {
    override suspend fun postOnboarding(member: OnboardingRequest): Result<Unit> =
        runCatching {
        }

    override suspend fun getOnboardingCheck(): Result<OnboardingCheckResponse> =
        runCatching {
            OnboardingCheckResponse(true)
        }
}
