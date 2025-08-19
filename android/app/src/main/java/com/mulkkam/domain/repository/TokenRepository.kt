package com.mulkkam.domain.repository

import com.mulkkam.domain.model.result.MulKkamResult

interface TokenRepository {
    suspend fun getAccessToken(): MulKkamResult<String?>

    suspend fun getRefreshToken(): MulKkamResult<String?>

    suspend fun getFcmToken(): MulKkamResult<String?>

    suspend fun saveAccessToken(token: String): MulKkamResult<Unit>

    suspend fun deleteAccessToken(): MulKkamResult<Unit>

    suspend fun saveRefreshToken(token: String): MulKkamResult<Unit>

    suspend fun deleteRefreshToken(): MulKkamResult<Unit>

    suspend fun saveFcmToken(token: String): MulKkamResult<Unit>

    suspend fun deleteFcmToken(): MulKkamResult<Unit>
}
