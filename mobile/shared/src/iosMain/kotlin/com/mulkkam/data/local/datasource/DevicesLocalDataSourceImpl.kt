package com.mulkkam.data.local.datasource

import com.mulkkam.data.local.userdefaults.DevicesUserDefaults
import platform.Foundation.NSUUID

class DevicesLocalDataSourceImpl(
    private val devicesUserDefaults: DevicesUserDefaults,
) : DevicesLocalDataSource {
    override var deviceUuid: String?
        get() = devicesUserDefaults.deviceUuid
        set(value) {
            value?.let { devicesUserDefaults.saveDeviceUuid(it) }
        }

    override var isNotificationGranted: Boolean
        get() = devicesUserDefaults.isNotificationGranted
        set(value) {
            devicesUserDefaults.saveNotificationGranted(value)
        }

    override var isFirstLaunch: Boolean
        get() = devicesUserDefaults.isFirstLaunch
        set(value) {
            devicesUserDefaults.saveIsFirstLaunch(value)
        }

    override fun saveDeviceUuid(uuid: String) {
        devicesUserDefaults.saveDeviceUuid(uuid)
    }

    override fun saveNotificationGranted(granted: Boolean) {
        devicesUserDefaults.saveNotificationGranted(granted)
    }

    override fun saveIsFirstLaunch(isFirstLaunch: Boolean) {
        devicesUserDefaults.saveIsFirstLaunch(isFirstLaunch)
    }

    override fun getOrCreateDeviceUuid(): String {
        val existingUuid = deviceUuid
        if (existingUuid != null) return existingUuid

        val newUuid = NSUUID().UUIDString
        saveDeviceUuid(newUuid)
        return newUuid
    }
}
