package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.api.safeApiCall
import com.mulkkam.data.remote.api.safeApiCallUnit
import com.mulkkam.data.remote.model.request.auth.AuthReissueRequest
import com.mulkkam.data.remote.model.request.auth.AuthRequest
import com.mulkkam.data.remote.model.response.auth.AuthReissueResponse
import com.mulkkam.data.remote.model.response.auth.AuthResponse
import com.mulkkam.domain.model.result.MulKkamResult
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : AuthRemoteDataSource {
    override suspend fun postAuthKakao(
        oauthAccessToken: String,
        deviceUuid: String,
    ): MulKkamResult<AuthResponse> =
        safeApiCall {
            httpClient.post("/auth/kakao") {
                setBody(AuthRequest(oauthAccessToken, deviceUuid))
            }
        }

    override suspend fun postAuthTokenReissue(
        refreshToken: String,
        deviceUuid: String,
    ): MulKkamResult<AuthReissueResponse> =
        safeApiCall {
            httpClient.post("/auth/token/reissue") {
                setBody(AuthReissueRequest(refreshToken, deviceUuid))
            }
        }

    override suspend fun postAuthLogout(): MulKkamResult<Unit> =
        safeApiCallUnit {
            httpClient.post("/auth/logout")
        }
}
