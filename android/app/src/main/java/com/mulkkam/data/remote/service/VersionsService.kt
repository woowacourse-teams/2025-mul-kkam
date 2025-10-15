package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.response.versions.MinimumVersionResponse
import retrofit2.http.GET

interface VersionsService {
    @GET("/versions")
    suspend fun getMinimumVersion(): Result<MinimumVersionResponse>
}
