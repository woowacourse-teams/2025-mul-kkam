package com.mulkkam.domain.repository

import com.mulkkam.domain.MulKkamResult

interface AuthRepository {
    suspend fun postAuthKakao(oauthAccessToken: String): MulKkamResult<String>
}
