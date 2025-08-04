package com.mulkkam.data.repository

import com.mulkkam.data.preference.TokenPreference

class TokenRepository(
    private val tokenPreference: TokenPreference,
) {
    fun getAccessToken(): String? = tokenPreference.accessToken

    fun getFcmToken(): String? = tokenPreference.fcmToken

    fun saveAccessToken(token: String) {
        tokenPreference.saveAccessToken(token)
    }

    fun deleteAccessToken() {
        tokenPreference.deleteAccessToken()
    }

    fun saveFcmToken(token: String) {
        tokenPreference.saveFcmToken(token)
    }

    fun deleteFcmToken() {
        tokenPreference.deleteFcmToken()
    }
}
