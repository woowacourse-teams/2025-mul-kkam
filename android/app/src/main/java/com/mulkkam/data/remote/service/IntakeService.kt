package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.intake.IntakeAmountRequest
import com.mulkkam.data.remote.model.request.intake.IntakeHistoryInputRequest
import com.mulkkam.data.remote.model.response.intake.IntakeHistoryResultResponse
import com.mulkkam.data.remote.model.response.intake.IntakeHistorySummaryResponse
import com.mulkkam.data.remote.model.response.intake.IntakeTargetAmountResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface IntakeService {
    @GET("/intake/history")
    suspend fun getIntakeHistory(
        @Query("from") from: String,
        @Query("to") to: String,
    ): Result<List<IntakeHistorySummaryResponse>>

    @POST("/intake/history/input")
    suspend fun postIntakeHistoryInput(
        @Body intakeHistory: IntakeHistoryInputRequest,
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
        @Query("gender") gender: String?,
        @Query("weight") weight: Double?,
    ): Result<IntakeTargetAmountResponse>

    @DELETE("/intake/history/details/{id}")
    suspend fun deleteIntakeHistoryDetails(
        @Path("id") id: Int,
    ): Result<Unit>
}
