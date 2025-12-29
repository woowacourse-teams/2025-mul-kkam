package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.response.versions.MinimumVersionResponse

// TODO: DataSource 구현 필요
class VersionRemoteDataSourceImpl : VersionRemoteDataSource {
    override suspend fun getMinimumVersion(): Result<MinimumVersionResponse> {
        TODO("Not yet implemented")
    }
}
