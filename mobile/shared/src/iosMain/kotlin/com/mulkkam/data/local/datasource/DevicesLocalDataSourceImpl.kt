package com.mulkkam.data.local.datasource

import platform.Foundation.NSUUID

// TODO: iOS 네이티브 구현 필요
// iOS에서는 UserDefaults를 사용하여 디바이스 정보를 저장해야 합니다.
class DevicesLocalDataSourceImpl : DevicesLocalDataSource {
    override var deviceUuid: String? = null

    override var isNotificationGranted: Boolean = false

    override var isFirstLaunch: Boolean = true

    override fun saveDeviceUuid(uuid: String) {
        // TODO: iOS UserDefaults 구현
        deviceUuid = uuid
    }

    override fun saveNotificationGranted(granted: Boolean) {
        // TODO: iOS UserDefaults 구현
        isNotificationGranted = granted
    }

    override fun saveIsFirstLaunch(isFirstLaunch: Boolean) {
        // TODO: iOS UserDefaults 구현
        this.isFirstLaunch = isFirstLaunch
    }

    fun getOrCreateDeviceUuid(): String {
        val existingUuid = deviceUuid
        if (existingUuid != null) return existingUuid

        val newUuid = NSUUID().UUIDString
        saveDeviceUuid(newUuid)
        return newUuid
    }
}
