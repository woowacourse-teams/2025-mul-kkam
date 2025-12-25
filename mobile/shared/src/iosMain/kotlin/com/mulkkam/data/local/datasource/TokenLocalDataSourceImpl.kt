package com.mulkkam.data.local.datasource

// TODO: iOS 네이티브 구현 필요
// iOS에서는 UserDefaults 또는 Keychain을 사용하여 토큰을 저장해야 합니다.
class TokenLocalDataSourceImpl : TokenLocalDataSource {
    override var accessToken: String? = null

    override var refreshToken: String? = null

    override var fcmToken: String? = null

    override fun saveAccessToken(token: String) {
        // TODO: iOS UserDefaults 또는 Keychain 구현
        accessToken = token
    }

    override fun deleteAccessToken() {
        // TODO: iOS UserDefaults 또는 Keychain 구현
        accessToken = null
    }

    override fun saveRefreshToken(token: String) {
        // TODO: iOS UserDefaults 또는 Keychain 구현
        refreshToken = token
    }

    override fun deleteRefreshToken() {
        // TODO: iOS UserDefaults 또는 Keychain 구현
        refreshToken = null
    }

    override fun saveFcmToken(token: String) {
        // TODO: iOS UserDefaults 또는 Keychain 구현
        fcmToken = token
    }

    override fun deleteFcmToken() {
        // TODO: iOS UserDefaults 또는 Keychain 구현
        fcmToken = null
    }
}
