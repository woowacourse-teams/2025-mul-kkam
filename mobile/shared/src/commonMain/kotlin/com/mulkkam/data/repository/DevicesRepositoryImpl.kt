package com.mulkkam.data.repository

import com.mulkkam.data.local.datasource.DevicesLocalDataSource
import com.mulkkam.data.remote.datasource.DevicesRemoteDataSource
import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.request.device.DeviceRequest
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.DevicesRepository

class DevicesRepositoryImpl(
    private val devicesRemoteDataSource: DevicesRemoteDataSource,
    private val devicesLocalDataSource: DevicesLocalDataSource,
) : DevicesRepository {
    override suspend fun postDevice(fcmToken: String): MulKkamResult<Unit> {
        val result =
            devicesRemoteDataSource.postDevice(
                DeviceRequest(fcmToken),
            )
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun deleteDevice(deviceId: String): MulKkamResult<Unit> {
        val result = devicesRemoteDataSource.deleteDevice(deviceId)
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun saveNotificationGranted(granted: Boolean): MulKkamResult<Unit> =
        runCatching {
            devicesLocalDataSource.saveNotificationGranted(granted)
        }.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )

    override suspend fun getNotificationGranted(): MulKkamResult<Boolean> =
        runCatching {
            devicesLocalDataSource.isNotificationGranted
        }.fold(
            onSuccess = { MulKkamResult(data = it) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )

    override suspend fun getDeviceUuid(): MulKkamResult<String> =
        runCatching {
            devicesLocalDataSource.getOrCreateDeviceUuid()
        }.fold(
            onSuccess = { MulKkamResult(data = it) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
}
