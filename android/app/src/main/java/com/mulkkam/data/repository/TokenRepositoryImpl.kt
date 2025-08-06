package com.mulkkam.data.repository

import com.mulkkam.data.preference.TokenPreference
import com.mulkkam.domain.repository.TokenRepository

class TokenRepositoryImpl(
    private val tokenPreference: TokenPreference,
) : TokenRepository {
    override fun getAccessToken(): String? = tokenPreference.accessToken

    override fun getFcmToken(): String? = tokenPreference.fcmToken

    override fun saveAccessToken(token: String) {
        tokenPreference.saveAccessToken(token)
    }

    override fun deleteAccessToken() {
        tokenPreference.deleteAccessToken()
    }

    override fun saveFcmToken(token: String) {
        tokenPreference.saveFcmToken(token)
    }

    override fun deleteFcmToken() {
        tokenPreference.deleteFcmToken()
    }
}
