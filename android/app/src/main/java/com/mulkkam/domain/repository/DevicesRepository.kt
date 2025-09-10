package com.mulkkam.domain.repository

import com.mulkkam.domain.model.result.MulKkamResult

interface DevicesRepository {
    suspend fun postDevice(fcmToken: String): MulKkamResult<Unit>

    suspend fun deleteDevice(deviceId: String): MulKkamResult<Unit>

    suspend fun saveNotificationGranted(granted: Boolean): MulKkamResult<Unit>

    suspend fun getNotificationGranted(): MulKkamResult<Boolean>

    suspend fun getDeviceUuid(): MulKkamResult<String>
}
