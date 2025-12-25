package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.response.auth.AuthReissueResponse
import com.mulkkam.data.remote.model.response.auth.AuthResponse
import com.mulkkam.domain.model.result.MulKkamResult

interface AuthRemoteDataSource {
    suspend fun postAuthKakao(
        oauthAccessToken: String,
        deviceUuid: String,
    ): MulKkamResult<AuthResponse>

    suspend fun postAuthTokenReissue(
        refreshToken: String,
        deviceUuid: String,
    ): MulKkamResult<AuthReissueResponse>

    suspend fun postAuthLogout(): MulKkamResult<Unit>
}
