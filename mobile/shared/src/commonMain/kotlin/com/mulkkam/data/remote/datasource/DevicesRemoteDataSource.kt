package com.mulkkam.data.remote.datasource

interface DevicesRemoteDataSource {
    suspend fun postDevice(fcmToken: String): Result<Unit>

    suspend fun deleteDevice(deviceId: String): Result<Unit>
}
