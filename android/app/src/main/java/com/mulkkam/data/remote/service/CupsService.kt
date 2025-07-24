package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.response.CupsResponse
import retrofit2.http.GET

interface CupsService {
    @GET("/cups")
    suspend fun getCups(): CupsResponse
}
