package com.mulkkam.di

import com.mulkkam.data.repository.DevicesRepositoryImpl
import com.mulkkam.data.repository.HealthRepositoryImpl
import com.mulkkam.data.repository.MembersRepositoryImpl
import com.mulkkam.domain.repository.DevicesRepository
import com.mulkkam.domain.repository.HealthRepository
import com.mulkkam.domain.repository.MembersRepository
import org.koin.dsl.module

val repositoryModule２ =
    module {
        single<HealthRepository> { HealthRepositoryImpl(get()) }
        single<DevicesRepository> { DevicesRepositoryImpl(get(), get()) }
        single<MembersRepository> { MembersRepositoryImpl(get(), get()) }
    }
