package com.mulkkam.data.local.datasource

import com.mulkkam.data.local.userdefaults.TokenUserDefaults

class TokenLocalDataSourceImpl(
    private val tokenUserDefaults: TokenUserDefaults,
) : TokenLocalDataSource {
    override var accessToken: String?
        get() = tokenUserDefaults.accessToken
        set(value) {
            value?.let { tokenUserDefaults.saveAccessToken(it) }
        }

    override var refreshToken: String?
        get() = tokenUserDefaults.refreshToken
        set(value) {
            value?.let { tokenUserDefaults.saveRefreshToken(it) }
        }

    override var fcmToken: String?
        get() = tokenUserDefaults.fcmToken
        set(value) {
            value?.let { tokenUserDefaults.saveFcmToken(it) }
        }

    override fun saveAccessToken(token: String) {
        tokenUserDefaults.saveAccessToken(token)
    }

    override fun deleteAccessToken() {
        tokenUserDefaults.deleteAccessToken()
    }

    override fun saveRefreshToken(token: String) {
        tokenUserDefaults.saveRefreshToken(token)
    }

    override fun deleteRefreshToken() {
        tokenUserDefaults.deleteRefreshToken()
    }

    override fun saveFcmToken(token: String) {
        tokenUserDefaults.saveFcmToken(token)
    }

    override fun deleteFcmToken() {
        tokenUserDefaults.deleteFcmToken()
    }
}
