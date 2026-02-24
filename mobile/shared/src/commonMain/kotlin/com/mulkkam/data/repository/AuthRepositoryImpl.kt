package com.mulkkam.data.repository

import com.mulkkam.data.remote.datasource.AuthRemoteDataSource
import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.response.auth.toDomain
import com.mulkkam.domain.model.auth.AuthInfo
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
) : AuthRepository {
    override suspend fun postAuthKakao(
        oauthAccessToken: String,
        deviceUuid: String,
    ): MulKkamResult<AuthInfo> {
        val result =
            authRemoteDataSource
                .postAuthKakao(
                    oauthAccessToken = oauthAccessToken,
                    deviceUuid = deviceUuid,
                )

        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun postAuthApple(
        authorizationCode: String,
        deviceUuid: String,
    ): MulKkamResult<AuthInfo> {
        val result =
            authRemoteDataSource
                .postAuthApple(
                    authorizationCode = authorizationCode,
                    deviceUuid = deviceUuid,
                )
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun postAuthLogout(): MulKkamResult<Unit> {
        val result =
            authRemoteDataSource.postAuthLogout()

        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }
}
