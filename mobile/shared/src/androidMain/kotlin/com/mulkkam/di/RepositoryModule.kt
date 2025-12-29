package com.mulkkam.di

import com.mulkkam.data.repository.AuthRepositoryImpl
import com.mulkkam.data.repository.DevicesRepositoryImpl
import com.mulkkam.data.repository.HealthRepositoryImpl
import com.mulkkam.data.repository.MembersRepositoryImpl
import com.mulkkam.data.repository.TokenRepositoryImpl
import com.mulkkam.domain.repository.AuthRepository
import com.mulkkam.domain.repository.DevicesRepository
import com.mulkkam.domain.repository.HealthRepository
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.domain.repository.TokenRepository
import org.koin.dsl.module

val repositoryModule =
    module {
        single<AuthRepository> { AuthRepositoryImpl(get()) }
        single<TokenRepository> { TokenRepositoryImpl(get()) }
        single<HealthRepository> { HealthRepositoryImpl(get()) }
        single<DevicesRepository> { DevicesRepositoryImpl(get(), get()) }
        single<MembersRepository> { MembersRepositoryImpl(get(), get()) }
    }
