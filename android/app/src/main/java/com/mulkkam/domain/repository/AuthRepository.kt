package com.mulkkam.domain.repository

import com.mulkkam.domain.model.auth.AuthTokenInfo
import com.mulkkam.domain.model.result.MulKkamResult

interface AuthRepository {
    suspend fun postAuthKakao(oauthAccessToken: String): MulKkamResult<AuthTokenInfo>

    suspend fun postAuthReissue(refreshToken: String): MulKkamResult<AuthTokenInfo>

    suspend fun postAuthLogout(): MulKkamResult<Unit>
}
