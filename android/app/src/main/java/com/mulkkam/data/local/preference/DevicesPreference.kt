package com.mulkkam.data.local.preference

import android.content.Context
import androidx.core.content.edit
import javax.inject.Inject

class DevicesPreference
    @Inject
    constructor(
        context: Context,
    ) {
        private val sharedPreference =
            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

        val isNotificationGranted: Boolean
            get() = sharedPreference.getBoolean(KEY_NOTIFICATION_GRANTED, false)

        val deviceUuid: String?
            get() = sharedPreference.getString(KEY_DEVICE_UUID, null)

        fun saveNotificationGranted(granted: Boolean) {
            sharedPreference.edit { putBoolean(KEY_NOTIFICATION_GRANTED, granted) }
        }

        fun removeNotificationGranted() {
            sharedPreference.edit { remove(KEY_NOTIFICATION_GRANTED) }
        }

        fun saveDeviceUuid(uuid: String) {
            sharedPreference.edit { putString(KEY_DEVICE_UUID, uuid) }
        }

        fun removeDeviceUuid() {
            sharedPreference.edit { remove(KEY_DEVICE_UUID) }
        }

        companion object {
            private const val PREFERENCE_NAME: String = "DEVICES_PREFERENCE"
            private const val KEY_NOTIFICATION_GRANTED: String = "NOTIFICATION_GRANTED"
            private const val KEY_DEVICE_UUID: String = "DEVICE_UUID"
        }
    }
