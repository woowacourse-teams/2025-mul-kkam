package com.mulkkam.data.repository

import com.mulkkam.data.remote.datasource.OnboardingRemoteDataSource
import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.request.onboarding.toData
import com.mulkkam.domain.model.OnboardingInfo
import com.mulkkam.domain.model.UserAuthState
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.OnboardingRepository

class OnboardingRepositoryImpl(
    private val onboardingRemoteDataSource: OnboardingRemoteDataSource,
) : OnboardingRepository {
    override suspend fun postOnboarding(onboardingInfo: OnboardingInfo): MulKkamResult<Unit> {
        val result = onboardingRemoteDataSource.postOnboarding(onboardingInfo.toData())

        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getOnboardingCheck(): MulKkamResult<UserAuthState> {
        val result = onboardingRemoteDataSource.getOnboardingCheck()
        return result.fold(
            onSuccess = { MulKkamResult(data = UserAuthState.from(it.finishedOnboarding)) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }
}
