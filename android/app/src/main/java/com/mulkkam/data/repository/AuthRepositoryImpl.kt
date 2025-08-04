package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.request.AuthRequest
import com.mulkkam.data.remote.service.AuthService
import com.mulkkam.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authService: AuthService,
) : AuthRepository {
    override suspend fun postAuthKakao(oauthAccessToken: String) =
        authService
            .postAuthKakao(
                AuthRequest(oauthAccessToken),
            ).accessToken
}
