package com.mulkkam.data.repository

import com.mulkkam.data.local.preference.TokenPreference
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.model.result.toMulKkamResult
import com.mulkkam.domain.repository.TokenRepository

class TokenRepositoryImpl(
    private val tokenPreference: TokenPreference,
) : TokenRepository {
    override suspend fun getAccessToken(): MulKkamResult<String?> =
        runCatching {
            tokenPreference.accessToken
        }.toMulKkamResult()

    override suspend fun getRefreshToken(): MulKkamResult<String?> =
        runCatching {
            tokenPreference.refreshToken
        }.toMulKkamResult()

    override suspend fun getFcmToken(): MulKkamResult<String?> =
        runCatching {
            tokenPreference.fcmToken
        }.toMulKkamResult()

    override suspend fun saveAccessToken(token: String): MulKkamResult<Unit> =
        runCatching {
            tokenPreference.saveAccessToken(token)
        }.toMulKkamResult()

    override suspend fun deleteAccessToken(): MulKkamResult<Unit> =
        runCatching {
            tokenPreference.deleteAccessToken()
        }.toMulKkamResult()

    override suspend fun saveRefreshToken(token: String): MulKkamResult<Unit> =
        runCatching {
            tokenPreference.saveRefreshToken(token)
        }.toMulKkamResult()

    override suspend fun deleteRefreshToken(): MulKkamResult<Unit> =
        runCatching {
            tokenPreference.deleteRefreshToken()
        }.toMulKkamResult()

    override suspend fun saveFcmToken(token: String): MulKkamResult<Unit> =
        runCatching {
            tokenPreference.saveFcmToken(token)
        }.toMulKkamResult()

    override suspend fun deleteFcmToken(): MulKkamResult<Unit> =
        runCatching {
            tokenPreference.deleteFcmToken()
        }.toMulKkamResult()
}
