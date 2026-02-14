package com.mulkkam.di

import com.mulkkam.data.logger.FakeLogger
import com.mulkkam.domain.logger.Logger
import org.koin.core.module.Module
import org.koin.dsl.module

val loggerModule: Module =
    module {
        single<Logger> { FakeLogger() }
    }
