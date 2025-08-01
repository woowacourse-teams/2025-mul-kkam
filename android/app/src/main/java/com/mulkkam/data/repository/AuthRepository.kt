package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.request.AuthRequest
import com.mulkkam.data.remote.service.AuthService

class AuthRepository(
    private val authService: AuthService,
) {
    suspend fun postAuth(accessToken: String) =
        authService
            .postAuth(
                AuthRequest(accessToken),
            ).accessToken
}
