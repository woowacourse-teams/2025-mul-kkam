package com.mulkkam.data.local.preference

import android.content.Context
import androidx.core.content.edit
import javax.inject.Inject

class TokenPreference
    @Inject
    constructor(
        context: Context,
    ) {
        private val sharedPreference =
            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

        val accessToken: String?
            get() = sharedPreference.getString(KEY_ACCESS_TOKEN, null)

        val refreshToken: String?
            get() = sharedPreference.getString(KEY_REFRESH_TOKEN, null)

        val fcmToken: String?
            get() = sharedPreference.getString(KEY_FCM_TOKEN, null)

        fun saveAccessToken(token: String) {
            sharedPreference.edit { putString(KEY_ACCESS_TOKEN, token) }
        }

        fun deleteAccessToken() {
            sharedPreference.edit { remove(KEY_ACCESS_TOKEN) }
        }

        fun saveRefreshToken(token: String) {
            sharedPreference.edit { putString(KEY_REFRESH_TOKEN, token) }
        }

        fun deleteRefreshToken() {
            sharedPreference.edit { remove(KEY_REFRESH_TOKEN) }
        }

        fun saveFcmToken(token: String) = sharedPreference.edit { putString(KEY_FCM_TOKEN, token) }

        fun deleteFcmToken() {
            sharedPreference.edit { remove(KEY_FCM_TOKEN) }
        }

        companion object {
            private const val PREFERENCE_NAME: String = "TOKEN_PREFERENCE"
            private const val KEY_ACCESS_TOKEN: String = "ACCESS_TOKEN"
            private const val KEY_REFRESH_TOKEN: String = "REFRESH_TOKEN"
            private const val KEY_FCM_TOKEN: String = "FCM_TOKEN"
        }
    }
