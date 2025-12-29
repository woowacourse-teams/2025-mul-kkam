package com.mulkkam.data.local.datasource

import android.content.Context
import androidx.core.content.edit
import java.util.UUID

class DevicesDataSourceImpl(
    context: Context,
) : DevicesLocalDataSource {
    private val sharedPreference =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    override var deviceUuid: String?
        get() = sharedPreference.getString(KEY_DEVICE_UUID, null)
        set(value) {
            sharedPreference.edit { putString(KEY_DEVICE_UUID, value) }
        }

    override var isNotificationGranted: Boolean
        get() = sharedPreference.getBoolean(KEY_NOTIFICATION_GRANTED, false)
        set(value) {
            sharedPreference.edit { putBoolean(KEY_NOTIFICATION_GRANTED, value) }
        }

    override var isFirstLaunch: Boolean
        get() = sharedPreference.getBoolean(KEY_IS_FIRST_LAUNCH, true)
        set(value) {
            sharedPreference.edit { putBoolean(KEY_IS_FIRST_LAUNCH, value) }
        }

    override fun saveDeviceUuid(uuid: String) {
        sharedPreference.edit { putString(KEY_DEVICE_UUID, uuid) }
    }

    override fun saveNotificationGranted(granted: Boolean) {
        sharedPreference.edit { putBoolean(KEY_NOTIFICATION_GRANTED, granted) }
    }

    override fun saveIsFirstLaunch(isFirstLaunch: Boolean) {
        sharedPreference.edit { putBoolean(KEY_IS_FIRST_LAUNCH, isFirstLaunch) }
    }

    fun getOrCreateDeviceUuid(): String {
        val existingUuid = deviceUuid
        if (existingUuid != null) return existingUuid

        val newUuid = UUID.randomUUID().toString()
        saveDeviceUuid(newUuid)
        return newUuid
    }

    companion object Companion {
        private const val PREFERENCE_NAME: String = "DEVICES_PREFERENCE"
        private const val KEY_DEVICE_UUID: String = "DEVICE_UUID"
        private const val KEY_NOTIFICATION_GRANTED: String = "NOTIFICATION_GRANTED"
        private const val KEY_IS_FIRST_LAUNCH: String = "IS_FIRST_LAUNCH"
    }
}
