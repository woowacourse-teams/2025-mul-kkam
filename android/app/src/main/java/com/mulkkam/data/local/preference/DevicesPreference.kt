package com.mulkkam.data.local.preference

import android.content.Context
import androidx.core.content.edit

class DevicesPreference(
    context: Context,
) {
    private val sharedPreference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    val isNotificationGranted: Boolean
        get() = sharedPreference.getBoolean(KEY_NOTIFICATION_GRANTED, false)

    fun saveNotificationGranted(granted: Boolean) {
        sharedPreference.edit { putBoolean(KEY_NOTIFICATION_GRANTED, granted) }
    }

    fun removeNotificationGranted() {
        sharedPreference.edit { remove(KEY_NOTIFICATION_GRANTED) }
    }

    companion object {
        private const val PREFERENCE_NAME: String = "DEVICES_PREFERENCE"
        private const val KEY_NOTIFICATION_GRANTED: String = "NOTIFICATION_GRANTED"
    }
}
