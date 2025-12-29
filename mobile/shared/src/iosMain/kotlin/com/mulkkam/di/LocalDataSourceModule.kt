package com.mulkkam.di

import com.mulkkam.data.local.datasource.DevicesLocalDataSource
import com.mulkkam.data.local.datasource.DevicesLocalDataSourceImpl
import com.mulkkam.data.local.datasource.TokenLocalDataSource
import com.mulkkam.data.local.datasource.TokenLocalDataSourceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val localDataSourceModule: Module =
    module {
        single<TokenLocalDataSource> { TokenLocalDataSourceImpl() }
        single<DevicesLocalDataSource> { DevicesLocalDataSourceImpl() }
    }
