package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.DeviceRequest
import com.mulkkam.data.remote.service.DevicesService
import com.mulkkam.domain.MulKkamResult
import com.mulkkam.domain.repository.DevicesRepository

class DevicesRepositoryImpl(
    private val devicesService: DevicesService,
) : DevicesRepository {
    override suspend fun postDevice(
        fcmToken: String,
        deviceId: String,
    ): MulKkamResult<Unit> {
        val result =
            devicesService.postDevice(
                DeviceRequest(fcmToken, deviceId),
            )
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }
}
