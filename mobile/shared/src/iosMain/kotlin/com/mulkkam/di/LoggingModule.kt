package com.mulkkam.di

import com.mulkkam.data.logger.LoggerImpl
import com.mulkkam.data.logger.SensitiveInfoSanitizerImpl
import com.mulkkam.domain.logger.LogSanitizer
import com.mulkkam.domain.logger.Logger
import org.koin.core.module.Module
import org.koin.dsl.module

fun loggingModule(isDebug: Boolean): Module =
    module {
        single<LogSanitizer> { SensitiveInfoSanitizerImpl() }
        single<Logger> { LoggerImpl(sanitizer = get(), isDebug = isDebug) }
    }
