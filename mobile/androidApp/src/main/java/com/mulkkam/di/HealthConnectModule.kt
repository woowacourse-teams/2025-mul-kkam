package com.mulkkam.di

import androidx.health.connect.client.HealthConnectClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

const val PROVIDER_PACKAGE_NAME =
    "com.google.android.apps.healthdata"

val healthConnectModule =
    module {
        single<HealthConnectClient?> {
            runCatching {
                HealthConnectClient.getOrCreate(androidContext())
            }.getOrNull()
        }
    }
