package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.api.safeApiCall
import com.mulkkam.data.remote.api.safeApiCallUnit
import com.mulkkam.data.remote.model.request.auth.AuthAppleRequest
import com.mulkkam.data.remote.model.request.auth.AuthReissueRequest
import com.mulkkam.data.remote.model.request.auth.AuthRequest
import com.mulkkam.data.remote.model.response.auth.AuthReissueResponse
import com.mulkkam.data.remote.model.response.auth.AuthResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : AuthRemoteDataSource {
    override suspend fun postAuthKakao(
        oauthAccessToken: String,
        deviceUuid: String,
    ): Result<AuthResponse> =
        safeApiCall {
            httpClient.post("/auth/kakao") {
                setBody(AuthRequest(oauthAccessToken, deviceUuid))
            }
        }

    override suspend fun postAuthApple(
        authorizationCode: String,
        deviceUuid: String,
    ): Result<AuthResponse> =
        safeApiCall {
            httpClient.post("/auth/apple") {
                setBody(AuthAppleRequest(authorizationCode, deviceUuid))
            }
        }

    override suspend fun postAuthTokenReissue(
        refreshToken: String,
        deviceUuid: String,
    ): Result<AuthReissueResponse> =
        safeApiCall {
            httpClient.post("/auth/token/reissue") {
                setBody(AuthReissueRequest(refreshToken, deviceUuid))
            }
        }

    override suspend fun postAuthLogout(): Result<Unit> =
        safeApiCallUnit {
            httpClient.post("/auth/logout")
        }
}
