package com.mulkkam.di

import org.koin.core.module.Module
import org.koin.dsl.module

fun iosSharedModule(
    baseUrl: String,
    isDebug: Boolean,
): Module =
    module {
        includes(
            loggerModule,
            repositoryModule,
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
