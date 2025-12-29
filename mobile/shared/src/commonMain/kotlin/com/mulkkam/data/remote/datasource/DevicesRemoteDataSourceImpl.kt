package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.request.device.DeviceRequest

// TODO: DataSource 구현 필요
class DevicesRemoteDataSourceImpl : DevicesRemoteDataSource {
    override suspend fun postDevice(deviceRequest: DeviceRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDevice(deviceId: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}
