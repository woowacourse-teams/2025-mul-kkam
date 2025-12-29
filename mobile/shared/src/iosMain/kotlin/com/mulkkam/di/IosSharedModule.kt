package com.mulkkam.di

import com.mulkkam.data.logger.FakeLogger
import com.mulkkam.domain.logger.Logger
import org.koin.core.module.Module
import org.koin.dsl.module

fun iosSharedModule(
    baseUrl: String,
    isDebug: Boolean,
): Module =
    module {
        single<Logger> {
            FakeLogger()
        }

        includes(
            httpClientEngineModule,
            commonNetworkModule(baseUrl),
            localDataSourceModule,
            remoteDataSourceModule,
            commonDataSourceModule,
            commonRepositoryModule,
        )
    }
