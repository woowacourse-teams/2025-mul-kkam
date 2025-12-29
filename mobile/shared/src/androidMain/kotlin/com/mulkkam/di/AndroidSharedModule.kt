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
            repositoryModule2,
            commonRepositoryModule,
            checkerModule2,
            healthConnectModule2,
            workerModule2,
            workManagerModule2,
            viewModelModule2,
        )
    }
