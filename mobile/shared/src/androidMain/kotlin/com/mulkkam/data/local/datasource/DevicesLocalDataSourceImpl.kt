package com.mulkkam.data.local.datasource

import com.mulkkam.data.local.preference.DevicesPreference

class DevicesLocalDataSourceImpl(
    private val devicesPreference: DevicesPreference,
) : DevicesLocalDataSource {

    override var deviceUuid: String?
        get() = devicesPreference.deviceUuid
        set(value) {
            value?.let { devicesPreference.saveDeviceUuid(it) }
        }

    override var isNotificationGranted: Boolean
        get() = devicesPreference.isNotificationGranted
        set(value) {
            devicesPreference.saveNotificationGranted(value)
        }

    override var isFirstLaunch: Boolean
        get() = devicesPreference.isFirstLaunch
        set(value) {
            devicesPreference.saveIsFirstLaunch(value)
        }

    override fun saveDeviceUuid(uuid: String) {
        devicesPreference.saveDeviceUuid(uuid)
    }

    override fun saveNotificationGranted(granted: Boolean) {
        devicesPreference.saveNotificationGranted(granted)
    }

    override fun saveIsFirstLaunch(isFirstLaunch: Boolean) {
        devicesPreference.saveIsFirstLaunch(isFirstLaunch)
    }
}
