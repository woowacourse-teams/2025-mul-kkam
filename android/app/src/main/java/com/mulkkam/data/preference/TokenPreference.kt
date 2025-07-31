package com.mulkkam.data.preference

import android.content.Context
import androidx.core.content.edit

class TokenPreference(
    context: Context,
) {
    private val sharedPreference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    val accessToken: String?
        get() = sharedPreference.getString(KEY_ACCESS_TOKEN, null)

    fun saveAccessToken(token: String) {
        sharedPreference.edit { putString(KEY_ACCESS_TOKEN, token) }
    }

    fun deleteAccessToken() {
        sharedPreference.edit { remove(KEY_ACCESS_TOKEN) }
    }

    companion object {
        private const val PREFERENCE_NAME: String = "TOKEN_PREFERENCE"
        private const val KEY_ACCESS_TOKEN: String = "ACCESS_TOKEN"
    }
}
