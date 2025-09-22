package com.mulkkam.di

import android.content.Context
import androidx.health.connect.client.HealthConnectClient

object HealthConnectInjection {
    private var _healthConnectClient: HealthConnectClient? = null
    val healthConnectClient: HealthConnectClient?
        get() = _healthConnectClient

    const val PROVIDER_PACKAGE_NAME: String = "com.google.android.apps.healthdata"

    fun init(context: Context) {
        runCatching {
            _healthConnectClient = HealthConnectClient.getOrCreate(context)
        }
    }
}
