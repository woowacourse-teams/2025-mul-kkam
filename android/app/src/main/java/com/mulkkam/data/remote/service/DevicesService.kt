package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.DeviceRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface DevicesService {
    @POST("/devices")
    suspend fun postDevice(
        @Body deviceRequest: DeviceRequest,
    ): Result<DeviceRequest>
}
