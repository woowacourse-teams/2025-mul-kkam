package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.auth.AuthRequest
import com.mulkkam.data.remote.model.response.auth.toDomain
import com.mulkkam.data.remote.service.AuthService
import com.mulkkam.domain.model.auth.AuthInfo
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authService: AuthService,
) : AuthRepository {
    override suspend fun postAuthKakao(oauthAccessToken: String): MulKkamResult<AuthInfo> {
        val result =
            authService
                .postAuthKakao(
                    AuthRequest(oauthAccessToken),
                )

        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun postAuthLogout(): MulKkamResult<Unit> {
        val result =
            authService.postAuthLogout()

        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }
}
