package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.response.IntakeHistorySummaryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface IntakeService {
    @GET("/intake/history")
    suspend fun getIntakeHistory(
        @Query("from") from: String?,
        @Query("to") to: String?,
    ): List<IntakeHistorySummaryResponse>
}
