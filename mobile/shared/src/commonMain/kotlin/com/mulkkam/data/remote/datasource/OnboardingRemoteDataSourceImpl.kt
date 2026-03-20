package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.api.safeApiCall
import com.mulkkam.data.remote.api.safeApiCallUnit
import com.mulkkam.data.remote.model.request.onboarding.OnboardingRequest
import com.mulkkam.data.remote.model.response.onboarding.OnboardingCheckResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class OnboardingRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : OnboardingRemoteDataSource {
    override suspend fun postOnboarding(member: OnboardingRequest): Result<Unit> =
        safeApiCallUnit {
            httpClient.post("/onboarding") {
                setBody(member)
            }
        }

    override suspend fun getOnboardingCheck(): Result<OnboardingCheckResponse> =
        safeApiCall {
            httpClient.get("/onboarding/check")
        }
}
