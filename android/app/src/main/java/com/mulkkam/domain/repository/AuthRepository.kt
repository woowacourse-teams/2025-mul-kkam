package com.mulkkam.domain.repository

import com.mulkkam.domain.model.auth.Tokens
import com.mulkkam.domain.model.result.MulKkamResult

interface AuthRepository {
    suspend fun postAuthKakao(oauthAccessToken: String): MulKkamResult<Tokens>

    suspend fun postAuthReissue(refreshToken: String): MulKkamResult<Tokens>
}
