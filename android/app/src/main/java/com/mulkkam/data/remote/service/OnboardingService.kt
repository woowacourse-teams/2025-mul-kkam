package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.onboarding.OnboardingRequest
import com.mulkkam.data.remote.model.response.onboarding.OnboardingCheckResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OnboardingService {
    @POST("/onboarding")
    suspend fun postOnboarding(
        @Body member: OnboardingRequest,
    ): Result<Unit>

    @GET("/onboarding/check/onboarding")
    suspend fun getOnboardingCheck(): Result<OnboardingCheckResponse>
}
