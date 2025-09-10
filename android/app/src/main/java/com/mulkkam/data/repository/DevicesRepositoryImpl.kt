package com.mulkkam.data.repository

import com.mulkkam.data.local.preference.DevicesPreference
import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.device.DeviceRequest
import com.mulkkam.data.remote.service.DevicesService
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.DevicesRepository
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.UUID

class DevicesRepositoryImpl(
    private val devicesService: DevicesService,
    private val devicesPreference: DevicesPreference,
) : DevicesRepository {
    override suspend fun postDevice(fcmToken: String): MulKkamResult<Unit> {
        val result =
            devicesService.postDevice(
                DeviceRequest(fcmToken),
            )
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun deleteDevice(deviceId: String): MulKkamResult<Unit> {
        val result = devicesService.deleteDevice(deviceId)
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun saveNotificationGranted(granted: Boolean): MulKkamResult<Unit> =
        runCatching {
            devicesPreference.saveNotificationGranted(granted)
        }.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )

    override suspend fun getNotificationGranted(): MulKkamResult<Boolean> =
        runCatching {
            devicesPreference.isNotificationGranted
        }.fold(
            onSuccess = { MulKkamResult(data = it) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )

    override suspend fun getDeviceUuid(): MulKkamResult<String> =
        runCatching {
            devicesPreference.deviceUuid ?: generateSha256Hash().also {
                devicesPreference.saveDeviceUuid(it)
            }
        }.fold(
            onSuccess = { MulKkamResult(data = it) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )

    private fun generateSha256Hash(): String {
        val uuid = UUID.randomUUID().toString()
        val hashBytes =
            MessageDigest
                .getInstance("SHA-256")
                .digest(uuid.toByteArray(StandardCharsets.UTF_8))
        return hashBytes.take(4).joinToString("") { "%02x".format(it) }
    }
}
