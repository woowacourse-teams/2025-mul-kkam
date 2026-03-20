package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.api.safeApiCallUnit
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class NicknameRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : NicknameRemoteDataSource {
    override suspend fun getNicknameValidation(nickname: String): Result<Unit> =
        safeApiCallUnit {
            httpClient.get("/nickname/validation") {
                parameter(
                    key = "nickname",
                    value = nickname,
                )
            }
        }
}
