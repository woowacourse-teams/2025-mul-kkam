package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.AddCupRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface CupsService {
    @POST("/cups")
    suspend fun postCups(
        @Body addCupRequest: AddCupRequest,
    )
}
