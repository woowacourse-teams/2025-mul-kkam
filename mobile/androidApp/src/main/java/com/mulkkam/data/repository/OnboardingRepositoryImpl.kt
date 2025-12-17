package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.onboarding.toData
import com.mulkkam.data.remote.service.OnboardingService
import com.mulkkam.domain.model.members.OnboardingInfo
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.OnboardingRepository
import com.mulkkam.ui.model.UserAuthState

class OnboardingRepositoryImpl(
    private val onboardingService: OnboardingService,
) : OnboardingRepository {
    override suspend fun postOnboarding(onboardingInfo: OnboardingInfo): MulKkamResult<Unit> {
        val result = onboardingService.postOnboarding(onboardingInfo.toData())

        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun getOnboardingCheck(): MulKkamResult<UserAuthState> {
        val result = onboardingService.getOnboardingCheck()
        return result.fold(
            onSuccess = { MulKkamResult(data = UserAuthState.from(it.finishedOnboarding)) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }
}
