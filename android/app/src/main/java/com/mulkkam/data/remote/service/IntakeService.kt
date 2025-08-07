package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.IntakeAmountRequest
import com.mulkkam.data.remote.model.request.IntakeAmountTargetRecommendedRequest
import com.mulkkam.data.remote.model.request.IntakeHistoryRequest
import com.mulkkam.data.remote.model.response.IntakeHistoryResultResponse
import com.mulkkam.data.remote.model.response.IntakeHistorySummaryResponse
import com.mulkkam.data.remote.model.response.IntakeTargetAmountResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface IntakeService {
    @GET("/intake/history")
    suspend fun getIntakeHistory(
        @Query("from") from: String,
        @Query("to") to: String,
    ): Result<List<IntakeHistorySummaryResponse>>

    @POST("/intake/history")
    suspend fun postIntakeHistory(
        @Body intakeHistory: IntakeHistoryRequest,
    ): Result<IntakeHistoryResultResponse>

    @PATCH("/intake/amount/target")
    suspend fun patchIntakeTarget(
        @Body intakeAmount: IntakeAmountRequest,
    ): Result<Unit>

    @GET("/intake/amount/target")
    suspend fun getIntakeTarget(): Result<IntakeTargetAmountResponse>

    @GET("/intake/amount/recommended")
    suspend fun getIntakeAmountRecommended(): Result<IntakeTargetAmountResponse>

    @GET("/intake/amount/target/recommended")
    suspend fun getIntakeAmountTargetRecommended(
        @Body intakeAmountTargetRecommended: IntakeAmountTargetRecommendedRequest,
    ): Result<IntakeTargetAmountResponse>
}
