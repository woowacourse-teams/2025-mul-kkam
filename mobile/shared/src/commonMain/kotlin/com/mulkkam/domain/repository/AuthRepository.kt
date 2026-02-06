package com.mulkkam.domain.repository

import com.mulkkam.domain.model.auth.AuthInfo
import com.mulkkam.domain.model.result.MulKkamResult

interface AuthRepository {
    suspend fun postAuthKakao(
        oauthAccessToken: String,
        deviceUuid: String,
    ): MulKkamResult<AuthInfo>

    suspend fun postAuthApple(
        authorizationCode: String,
        deviceUuid: String,
    ): MulKkamResult<AuthInfo>

    suspend fun postAuthLogout(): MulKkamResult<Unit>
}
