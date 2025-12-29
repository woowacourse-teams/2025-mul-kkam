package com.mulkkam.di

import org.koin.core.module.Module
import org.koin.dsl.module

fun iosSharedModule(baseUrl: String): Module =
    module {
        includes(
            localDataSourceModule,
            networkModule(baseUrl),
            remoteDataSourceModule,
            commonRepositoryModule,
            commonDataSourceModule,
        )
    }
