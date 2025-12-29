package com.mulkkam.di

import com.mulkkam.data.repository.HealthRepositoryImpl
import com.mulkkam.domain.repository.HealthRepository
import org.koin.dsl.module

val repositoryModule =
    module {
        single<HealthRepository> { HealthRepositoryImpl(get()) }
    }
