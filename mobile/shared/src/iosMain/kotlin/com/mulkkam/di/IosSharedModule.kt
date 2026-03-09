package com.mulkkam.di

import org.koin.core.module.Module
import org.koin.dsl.module

fun iosSharedModule(
    baseUrl: String,
    isDebug: Boolean,
): Module =
    module {
        includes(
            loggingModule(isDebug),
            repositoryModule,
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
