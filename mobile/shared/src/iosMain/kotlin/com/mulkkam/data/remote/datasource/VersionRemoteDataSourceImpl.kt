package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.api.safeApiCall
import com.mulkkam.data.remote.model.response.versions.MinimumVersionResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class VersionRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : VersionRemoteDataSource {
    override suspend fun getMinimumVersion(): Result<MinimumVersionResponse> =
        safeApiCall {
            httpClient.get("/versions/ios")
        }
}
