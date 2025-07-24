package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.IntakeAmountRequest
import com.mulkkam.data.remote.model.response.IntakeHistorySummaryResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface IntakeService {
    @GET("/intake/history")
    suspend fun getIntakeHistory(
        @Query("from") from: String?,
        @Query("to") to: String?,
    ): List<IntakeHistorySummaryResponse>

    @PATCH("/intake/amount/target")
    suspend fun patchIntakeTarget(
        @Body intakeAmount: IntakeAmountRequest,
    )
}
