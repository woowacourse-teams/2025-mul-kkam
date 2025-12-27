package com.mulkkam.data.local.datasource

import android.content.Context
import androidx.core.content.edit

class TokenLocalDataSourceImpl(
    context: Context,
) : TokenLocalDataSource {
    private val sharedPreference =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    override var accessToken: String?
        get() = sharedPreference.getString(KEY_ACCESS_TOKEN, null)
        set(value) {
            sharedPreference.edit { putString(KEY_ACCESS_TOKEN, value) }
        }

    override var refreshToken: String?
        get() = sharedPreference.getString(KEY_REFRESH_TOKEN, null)
        set(value) {
            sharedPreference.edit { putString(KEY_REFRESH_TOKEN, value) }
        }

    override var fcmToken: String?
        get() = sharedPreference.getString(KEY_FCM_TOKEN, null)
        set(value) {
            sharedPreference.edit { putString(KEY_FCM_TOKEN, value) }
        }

    override fun saveAccessToken(token: String) {
        sharedPreference.edit { putString(KEY_ACCESS_TOKEN, token) }
    }

    override fun deleteAccessToken() {
        sharedPreference.edit { remove(KEY_ACCESS_TOKEN) }
    }

    override fun saveRefreshToken(token: String) {
        sharedPreference.edit { putString(KEY_REFRESH_TOKEN, token) }
    }

    override fun deleteRefreshToken() {
        sharedPreference.edit { remove(KEY_REFRESH_TOKEN) }
    }

    override fun saveFcmToken(token: String) {
        sharedPreference.edit { putString(KEY_FCM_TOKEN, token) }
    }

    override fun deleteFcmToken() {
        sharedPreference.edit { remove(KEY_FCM_TOKEN) }
    }

    companion object {
        private const val PREFERENCE_NAME: String = "TOKEN_PREFERENCE"
        private const val KEY_ACCESS_TOKEN: String = "ACCESS_TOKEN"
        private const val KEY_REFRESH_TOKEN: String = "REFRESH_TOKEN"
        private const val KEY_FCM_TOKEN: String = "FCM_TOKEN"
    }
}
