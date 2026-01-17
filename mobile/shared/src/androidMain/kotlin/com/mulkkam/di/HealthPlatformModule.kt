package com.mulkkam.di

import androidx.health.connect.client.HealthConnectClient
import com.mulkkam.data.local.health.HealthApi
import com.mulkkam.data.local.health.HealthApiImpl
import com.mulkkam.domain.model.bio.HealthPlatform
import com.mulkkam.ui.setting.bioinfo.HealthConnectPlatform
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

const val PROVIDER_PACKAGE_NAME =
    "com.google.android.apps.healthdata"

val healthPlatformModule =
    module {
        single<HealthApi> {
            HealthApiImpl(
                runCatching {
                    HealthConnectClient.getOrCreate(androidContext())
                }.getOrNull(),
            )
        }
    }

val healthManagerModule =
    module {
        single<HealthPlatform> {
            HealthConnectPlatform(androidContext())
        }
    }
