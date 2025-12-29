package com.mulkkam.di

import org.koin.core.module.Module
import org.koin.dsl.module

fun androidSharedModule(
    baseUrl: String,
    isDebug: Boolean,
): Module =
    module {
        includes(
            httpClientEngineModule,
            preferenceModule,
            localDataSourceModule,
            loggingModule(isDebug),
            remoteDataSourceModule,
            commonDataSourceModule,
            commonNetworkModule(baseUrl),
            repositoryModule,
            commonRepositoryModule,
            checkerModule,
            healthPlatformModule,
            workerModule,
            workManagerModule,
            commonViewModelModule,
        )
    }
