package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.response.auth.AuthReissueResponse
import com.mulkkam.data.remote.model.response.auth.AuthResponse

interface AuthRemoteDataSource {
    suspend fun postAuthKakao(
        oauthAccessToken: String,
        deviceUuid: String,
    ): Result<AuthResponse>

    suspend fun postAuthTokenReissue(
        refreshToken: String,
        deviceUuid: String,
    ): Result<AuthReissueResponse>

    suspend fun postAuthLogout(): Result<Unit>
}
