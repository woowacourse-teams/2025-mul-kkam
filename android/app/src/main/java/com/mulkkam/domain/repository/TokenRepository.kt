package com.mulkkam.domain.repository

interface TokenRepository {
    fun getAccessToken(): String?

    fun getFcmToken(): String?

    fun saveAccessToken(token: String)

    fun deleteAccessToken()

    fun saveFcmToken(token: String)

    fun deleteFcmToken()
}
