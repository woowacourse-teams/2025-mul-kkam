package com.mulkkam.domain.repository

import com.mulkkam.domain.model.result.MulKkamResult

interface DevicesRepository {
    suspend fun postDevice(
        fcmToken: String,
        deviceId: String,
    ): MulKkamResult<Unit>
}
