package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.api.safeApiCallUnit
import com.mulkkam.data.remote.model.request.device.DeviceRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class DevicesRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : DevicesRemoteDataSource {
    override suspend fun postDevice(fcmToken: String): Result<Unit> =
        safeApiCallUnit {
            httpClient.post("/devices") {
                setBody(DeviceRequest(fcmToken, PLATFORM_NAME))
            }
        }

    override suspend fun deleteDevice(deviceId: String): Result<Unit> =
        safeApiCallUnit {
            httpClient.delete("/devices/fcm-token") {
                header("X-Device-Id", deviceId)
            }
        }

    companion object {
        private const val PLATFORM_NAME: String = "ANDROID"
    }
}
