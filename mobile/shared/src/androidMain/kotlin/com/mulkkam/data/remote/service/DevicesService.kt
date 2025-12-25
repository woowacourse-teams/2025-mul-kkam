package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.device.DeviceRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST

interface DevicesService {
    @POST("/devices")
    suspend fun postDevice(
        @Body deviceRequest: DeviceRequest,
    ): Result<Unit>

    @DELETE("/devices/fcm-token")
    suspend fun deleteDevice(
        @Header("X-Device-Id") deviceId: String,
    ): Result<Unit>
}
