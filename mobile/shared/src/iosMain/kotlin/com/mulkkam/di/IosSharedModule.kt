package com.mulkkam.di

import com.mulkkam.domain.logger.FirebaseLoggingBridge
import org.koin.core.module.Module
import org.koin.dsl.module

fun iosSharedModule(
    baseUrl: String,
    isDebug: Boolean,
    firebaseBridge: FirebaseLoggingBridge,
): Module =
    module {
        includes(
            loggingModule(isDebug, firebaseBridge),
            checkerModule,
            healthManagerModule,
            httpClientEngineModule,
            commonNetworkModule(baseUrl),
            localDataSourceModule,
            remoteDataSourceModule,
            commonDataSourceModule,
            commonRepositoryModule,
            commonViewModelModule,
            userDefaultsModule,
        )
    }
