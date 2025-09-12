package com.mulkkam.data.local.preference

import android.content.Context
import androidx.core.content.edit

class MembersPreference(
    context: Context,
) {
    private val sharedPreference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    val isFirstLaunch: Boolean
        get() = sharedPreference.getBoolean(KEY_FIRST_LAUNCH, true)

    fun saveIsFirstLaunch() {
        sharedPreference.edit { putBoolean(KEY_FIRST_LAUNCH, false) }
    }

    companion object {
        private const val PREFERENCE_NAME: String = "MEMBER_PREFERENCE"
        private const val KEY_FIRST_LAUNCH: String = "FIRST_LAUNCH"
    }
}
