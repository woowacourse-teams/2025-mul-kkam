package com.mulkkam.data.local.datasource

import com.mulkkam.data.local.preference.DevicesPreference
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.UUID

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

    override fun getOrCreateDeviceUuid(): String = deviceUuid ?: generateSha256Hash().also { saveDeviceUuid(it) }

    override fun saveDeviceUuid(uuid: String) {
        devicesPreference.saveDeviceUuid(uuid)
    }

    override fun saveNotificationGranted(granted: Boolean) {
        devicesPreference.saveNotificationGranted(granted)
    }

    override fun saveIsFirstLaunch(isFirstLaunch: Boolean) {
        devicesPreference.saveIsFirstLaunch(isFirstLaunch)
    }

    private fun generateSha256Hash(): String {
        val uuid = UUID.randomUUID().toString()
        val hashBytes =
            MessageDigest
                .getInstance(HASH_ALGORITHM)
                .digest(uuid.toByteArray(CHARSET))
        return hashBytes.take(HASH_LENGTH).joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val HASH_ALGORITHM: String = "SHA-256"
        private const val HASH_LENGTH: Int = 4
        private val CHARSET: Charset = StandardCharsets.UTF_8
    }
}
