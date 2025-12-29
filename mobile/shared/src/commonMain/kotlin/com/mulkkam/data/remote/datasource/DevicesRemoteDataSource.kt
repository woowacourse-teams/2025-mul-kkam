package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.request.device.DeviceRequest

interface DevicesRemoteDataSource {
    suspend fun postDevice(deviceRequest: DeviceRequest): Result<Unit>

    suspend fun deleteDevice(deviceId: String): Result<Unit>
}
