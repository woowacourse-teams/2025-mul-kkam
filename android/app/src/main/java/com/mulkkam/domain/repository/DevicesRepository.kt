package com.mulkkam.domain.repository

import com.mulkkam.domain.MulKkamResult

interface DevicesRepository {
    suspend fun postDevice(
        fcmToken: String,
        deviceId: String,
    ): MulKkamResult<Unit>
}
