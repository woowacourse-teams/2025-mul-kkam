package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.AuthRequest
import com.mulkkam.data.remote.service.AuthService
import com.mulkkam.domain.model.MulKkamResult
import com.mulkkam.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authService: AuthService,
) : AuthRepository {
    override suspend fun postAuthKakao(oauthAccessToken: String): MulKkamResult<String> {
        val result =
            authService
                .postAuthKakao(
                    AuthRequest(oauthAccessToken),
                )

        return result.fold(
            onSuccess = { MulKkamResult(data = it.accessToken) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }
}
