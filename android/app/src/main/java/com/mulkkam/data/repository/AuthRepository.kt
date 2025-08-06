package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.request.AuthRequest
import com.mulkkam.data.remote.service.AuthService

class AuthRepository(
    private val authService: AuthService,
) {
    suspend fun postAuthKakao(oauthAccessToken: String) =
        authService
            .postAuthKakao(
                AuthRequest(oauthAccessToken),
            ).accessToken
}
