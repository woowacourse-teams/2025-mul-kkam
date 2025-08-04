package com.mulkkam.domain.repository

interface AuthRepository {
    suspend fun postAuthKakao(oauthAccessToken: String): String
}
