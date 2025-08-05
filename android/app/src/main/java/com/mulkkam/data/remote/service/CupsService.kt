package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.AddCupRequest
import com.mulkkam.data.remote.model.response.CupsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CupsService {
    @GET("/cups")
    suspend fun getCups(): Result<CupsResponse>

    @POST("/cups")
    suspend fun postCups(
        @Body addCupRequest: AddCupRequest,
    ): Result<Unit>
}
