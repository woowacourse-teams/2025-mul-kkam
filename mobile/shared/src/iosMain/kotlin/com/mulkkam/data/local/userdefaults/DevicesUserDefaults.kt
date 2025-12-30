package com.mulkkam.data.local.userdefaults

import platform.Foundation.NSUserDefaults

class DevicesUserDefaults {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    val isNotificationGranted: Boolean
        get() = userDefaults.boolForKey(KEY_NOTIFICATION_GRANTED)

    val deviceUuid: String?
        get() = userDefaults.stringForKey(KEY_DEVICE_UUID)

    val isFirstLaunch: Boolean
        get() = userDefaults.boolForKey(KEY_IS_FIRST_LAUNCH)

    fun saveNotificationGranted(granted: Boolean) {
        userDefaults.setBool(granted, KEY_NOTIFICATION_GRANTED)
    }

    fun removeNotificationGranted() {
        userDefaults.removeObjectForKey(KEY_NOTIFICATION_GRANTED)
    }

    fun saveDeviceUuid(uuid: String) {
        userDefaults.setObject(uuid, KEY_DEVICE_UUID)
    }

    fun removeDeviceUuid() {
        userDefaults.removeObjectForKey(KEY_DEVICE_UUID)
    }

    fun saveIsFirstLaunch(isFirstLaunch: Boolean) {
        userDefaults.setObject(isFirstLaunch, KEY_IS_FIRST_LAUNCH)
    }

    companion object {
        private const val KEY_NOTIFICATION_GRANTED: String = "NOTIFICATION_GRANTED"
        private const val KEY_DEVICE_UUID: String = "DEVICE_UUID"
        private const val KEY_IS_FIRST_LAUNCH: String = "IS_FIRST_LAUNCH"
    }
}
