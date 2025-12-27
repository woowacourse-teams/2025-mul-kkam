package com.mulkkam.di

import com.mulkkam.data.remote.datasource.AuthRemoteDataSource
import com.mulkkam.data.remote.datasource.AuthRemoteDataSourceImpl
import com.mulkkam.data.repository.AuthRepositoryImpl
import com.mulkkam.data.repository.TokenRepositoryImpl
import com.mulkkam.domain.repository.AuthRepository
import com.mulkkam.domain.repository.TokenRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val sharedDataSourceModule: Module =
    module {
        single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }
    }

val sharedRepositoryModule: Module =
    module {
        single<AuthRepository> { AuthRepositoryImpl(get()) }
        single<TokenRepository> { TokenRepositoryImpl(get()) }
    }

val sharedModule: Module =
    module {
        includes(sharedDataSourceModule, sharedRepositoryModule)
    }
