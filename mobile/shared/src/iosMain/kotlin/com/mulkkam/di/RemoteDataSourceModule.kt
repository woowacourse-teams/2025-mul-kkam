package com.mulkkam.di

import com.mulkkam.data.remote.datasource.AuthRemoteDataSource
import com.mulkkam.data.remote.datasource.AuthRemoteDataSourceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val remoteDataSourceModule: Module =
    module {
        single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }
    }
