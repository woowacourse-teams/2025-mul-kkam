package com.mulkkam.data.local.datasource

import com.mulkkam.data.local.preference.TokenPreference

class TokenLocalDataSourceImpl(
    private val tokenPreference: TokenPreference,
) : TokenLocalDataSource {
    override var accessToken: String?
        get() = tokenPreference.accessToken
        set(value) {
            value?.let { tokenPreference.saveAccessToken(it) }
        }

    override var refreshToken: String?
        get() = tokenPreference.refreshToken
        set(value) {
            value?.let { tokenPreference.saveRefreshToken(it) }
        }

    override var fcmToken: String?
        get() = tokenPreference.fcmToken
        set(value) {
            value?.let { tokenPreference.saveFcmToken(it) }
        }

    override fun saveAccessToken(token: String) {
        tokenPreference.saveAccessToken(token)
    }

    override fun deleteAccessToken() {
        tokenPreference.deleteAccessToken()
    }

    override fun saveRefreshToken(token: String) {
        tokenPreference.saveRefreshToken(token)
    }

    override fun deleteRefreshToken() {
        tokenPreference.deleteRefreshToken()
    }

    override fun saveFcmToken(token: String) {
        tokenPreference.saveFcmToken(token)
    }

    override fun deleteFcmToken() {
        tokenPreference.deleteFcmToken()
    }
}
