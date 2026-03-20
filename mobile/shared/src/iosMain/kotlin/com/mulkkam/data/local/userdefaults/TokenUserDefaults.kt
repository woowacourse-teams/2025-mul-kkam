package com.mulkkam.data.local.userdefaults

import platform.Foundation.NSUserDefaults

class TokenUserDefaults {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    val accessToken: String?
        get() = userDefaults.stringForKey(KEY_ACCESS_TOKEN)

    val refreshToken: String?
        get() = userDefaults.stringForKey(KEY_REFRESH_TOKEN)

    val fcmToken: String?
        get() = userDefaults.stringForKey(KEY_FCM_TOKEN)

    fun saveAccessToken(token: String) {
        userDefaults.setObject(token, KEY_ACCESS_TOKEN)
    }

    fun deleteAccessToken() {
        userDefaults.removeObjectForKey(KEY_ACCESS_TOKEN)
    }

    fun saveRefreshToken(token: String) {
        userDefaults.setObject(token, KEY_REFRESH_TOKEN)
    }

    fun deleteRefreshToken() {
        userDefaults.removeObjectForKey(KEY_REFRESH_TOKEN)
    }

    fun saveFcmToken(token: String) {
        userDefaults.setObject(token, KEY_FCM_TOKEN)
    }

    fun deleteFcmToken() {
        userDefaults.removeObjectForKey(KEY_FCM_TOKEN)
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val KEY_REFRESH_TOKEN = "REFRESH_TOKEN"
        private const val KEY_FCM_TOKEN = "FCM_TOKEN"
    }
}
