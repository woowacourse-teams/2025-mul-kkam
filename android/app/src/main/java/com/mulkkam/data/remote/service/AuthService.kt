package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.AuthRequest
import com.mulkkam.data.remote.model.response.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/auth/kakao")
    suspend fun postAuth(
        @Body authRequest: AuthRequest,
    ): AuthResponse
}
