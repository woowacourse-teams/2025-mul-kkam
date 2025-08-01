package com.mulkkam.data.preference

import android.content.Context
import androidx.core.content.edit

class TokenPreference(
    context: Context,
) {
    private val sharedPreference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    val accessToken: String?
        get() = sharedPreference.getString(ACCESS_TOKEN_KEY, null)

    val fcmToken: String?
        get() = sharedPreference.getString(FCM_TOKEN_KEY, null)

    fun saveAccessToken(token: String) {
        sharedPreference.edit { putString(ACCESS_TOKEN_KEY, token) }
    }

    fun deleteAccessToken() {
        sharedPreference.edit { remove(ACCESS_TOKEN_KEY) }
    }

    fun saveFcmToken(token: String) = sharedPreference.edit { putString(FCM_TOKEN_KEY, token) }

    fun deleteFcmToken() {
        sharedPreference.edit { remove(FCM_TOKEN_KEY) }
    }

    companion object {
        private const val PREFERENCE_NAME: String = "TOKEN_PREFERENCE"
        private const val ACCESS_TOKEN_KEY: String = "ACCESS_TOKEN"
        private const val FCM_TOKEN_KEY: String = "FCM_TOKEN"
    }
}
