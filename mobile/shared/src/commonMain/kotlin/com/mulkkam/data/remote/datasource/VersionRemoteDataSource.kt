package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.response.versions.MinimumVersionResponse

interface VersionRemoteDataSource {
    suspend fun getMinimumVersion(): Result<MinimumVersionResponse>
}
