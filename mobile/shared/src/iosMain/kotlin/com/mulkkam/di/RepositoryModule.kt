package com.mulkkam.di

import com.mulkkam.data.repository.HealthRepositoryImpl
import com.mulkkam.domain.repository.HealthRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val repositoryModule: Module =
    module {
        single<HealthRepository> { HealthRepositoryImpl() }
    }
