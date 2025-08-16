package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.auth.AuthReissueRequest
import com.mulkkam.data.remote.model.request.auth.AuthRequest
import com.mulkkam.data.remote.model.response.auth.AuthReissueResponse
import com.mulkkam.data.remote.model.response.auth.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/auth/kakao")
    suspend fun postAuthKakao(
        @Body authRequest: AuthRequest,
    ): Result<AuthResponse>

    @POST("/auth/token/reissue")
    suspend fun postAuthTokenReissue(
        @Body authReissueRequest: AuthReissueRequest,
    ): Result<AuthReissueResponse>

    @POST("/auth/logout")
    suspend fun postAuthLogout(): Result<Unit>
}
