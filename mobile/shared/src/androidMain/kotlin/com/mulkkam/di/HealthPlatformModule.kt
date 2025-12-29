package com.mulkkam.di

import androidx.health.connect.client.HealthConnectClient
import com.mulkkam.data.local.health.HealthPlatform
import com.mulkkam.data.local.health.HealthPlatformImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

const val PROVIDER_PACKAGE_NAME =
    "com.google.android.apps.healthdata"

val healthPlatformModule =
    module {
        single<HealthPlatform> {
            HealthPlatformImpl(
                runCatching {
                    HealthConnectClient.getOrCreate(androidContext())
                }.getOrNull(),
            )
        }
    }
