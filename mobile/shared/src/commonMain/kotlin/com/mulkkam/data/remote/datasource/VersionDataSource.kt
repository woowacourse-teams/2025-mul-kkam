package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.response.versions.MinimumVersionResponse

interface VersionDataSource {
    suspend fun getMinimumVersion(): Result<MinimumVersionResponse>
}
