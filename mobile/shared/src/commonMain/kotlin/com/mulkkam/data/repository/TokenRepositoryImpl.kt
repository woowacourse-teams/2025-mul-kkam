package com.mulkkam.data.repository

import com.mulkkam.data.local.datasource.TokenLocalDataSource
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.model.result.toMulKkamResult
import com.mulkkam.domain.repository.TokenRepository

class TokenRepositoryImpl(
    private val tokenLocalDataSource: TokenLocalDataSource,
) : TokenRepository {
    override suspend fun getAccessToken(): MulKkamResult<String?> =
        runCatching {
            tokenLocalDataSource.accessToken
        }.toMulKkamResult()

    override suspend fun getRefreshToken(): MulKkamResult<String?> =
        runCatching {
            tokenLocalDataSource.refreshToken
        }.toMulKkamResult()

    override suspend fun getFcmToken(): MulKkamResult<String?> =
        runCatching {
            tokenLocalDataSource.fcmToken
        }.toMulKkamResult()

    override suspend fun saveAccessToken(token: String): MulKkamResult<Unit> =
        runCatching {
            tokenLocalDataSource.saveAccessToken(token)
        }.toMulKkamResult()

    override suspend fun deleteAccessToken(): MulKkamResult<Unit> =
        runCatching {
            tokenLocalDataSource.deleteAccessToken()
        }.toMulKkamResult()

    override suspend fun saveRefreshToken(token: String): MulKkamResult<Unit> =
        runCatching {
            tokenLocalDataSource.saveRefreshToken(token)
        }.toMulKkamResult()

    override suspend fun deleteRefreshToken(): MulKkamResult<Unit> =
        runCatching {
            tokenLocalDataSource.deleteRefreshToken()
        }.toMulKkamResult()

    override suspend fun saveFcmToken(token: String): MulKkamResult<Unit> =
        runCatching {
            tokenLocalDataSource.saveFcmToken(token)
        }.toMulKkamResult()

    override suspend fun deleteFcmToken(): MulKkamResult<Unit> =
        runCatching {
            tokenLocalDataSource.deleteFcmToken()
        }.toMulKkamResult()
}
