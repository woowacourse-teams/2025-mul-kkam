package com.mulkkam.data.local.datasource

interface DevicesLocalDataSource {
    var deviceUuid: String?

    var isNotificationGranted: Boolean

    var isFirstLaunch: Boolean

    fun saveDeviceUuid(uuid: String)

    fun saveNotificationGranted(granted: Boolean)

    fun saveIsFirstLaunch(isFirstLaunch: Boolean)
}
