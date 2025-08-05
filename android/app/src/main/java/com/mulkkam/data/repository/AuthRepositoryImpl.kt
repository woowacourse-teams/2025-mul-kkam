package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.request.AuthRequest
import com.mulkkam.data.remote.service.AuthService
import com.mulkkam.domain.MulKkamError
import com.mulkkam.domain.MulKkamResult
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
            onFailure = { MulKkamResult(error = it as? MulKkamError ?: MulKkamError.Unknown) },
        )
    }
}
