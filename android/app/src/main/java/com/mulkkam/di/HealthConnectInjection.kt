package com.mulkkam.di

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import com.mulkkam.ui.util.extensions.isHealthConnectAvailable

object HealthConnectInjection {
    private var _healthConnectClient: HealthConnectClient? = null
    val healthConnectClient: HealthConnectClient
        get() = _healthConnectClient ?: throw IllegalStateException()

    const val PROVIDER_PACKAGE_NAME: String = "com.google.android.apps.healthdata"

    fun init(context: Context) {
        if (context.isHealthConnectAvailable() && _healthConnectClient == null) {
            _healthConnectClient = HealthConnectClient.getOrCreate(context)
        }
    }
}
