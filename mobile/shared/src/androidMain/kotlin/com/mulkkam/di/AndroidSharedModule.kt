package com.mulkkam.di

import org.koin.core.module.Module
import org.koin.dsl.module

fun androidSharedModule(
    baseUrl: String,
    isDebug: Boolean,
): Module =
    module {
        includes(
            preferenceModule2,
            localDataSourceModule,
            remoteDataSourceModule,
            networkModule(baseUrl),
            loggingModule(isDebug),
            repositoryModule2,
            checkerModule2,
            healthConnectModule2,
            serviceModule2,
            workerModule2,
            workManagerModule2,
            viewModelModule2,
            commonRepositoryModule,
            commonDataSourceModule,
        )
    }
