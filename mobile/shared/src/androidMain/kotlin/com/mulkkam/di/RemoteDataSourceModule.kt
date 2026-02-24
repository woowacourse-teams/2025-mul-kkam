package com.mulkkam.di

import com.mulkkam.data.remote.datasource.AuthRemoteDataSource
import com.mulkkam.data.remote.datasource.AuthRemoteDataSourceImpl
import com.mulkkam.data.remote.datasource.DevicesRemoteDataSource
import com.mulkkam.data.remote.datasource.DevicesRemoteDataSourceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val remoteDataSourceModule: Module =
    module {
        single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }
        single<DevicesRemoteDataSource> { DevicesRemoteDataSourceImpl(get()) }
    }
