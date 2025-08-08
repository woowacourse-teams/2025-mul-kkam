package com.mulkkam.domain.repository

import com.mulkkam.domain.model.MulKkamResult

interface AuthRepository {
    suspend fun postAuthKakao(oauthAccessToken: String): MulKkamResult<String>
}
