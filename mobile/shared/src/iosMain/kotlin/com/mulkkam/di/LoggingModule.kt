package com.mulkkam.di

import com.mulkkam.data.logger.LoggerImpl
import com.mulkkam.data.logger.SensitiveInfoSanitizerImpl
import com.mulkkam.domain.logger.FirebaseLoggingBridge
import com.mulkkam.domain.logger.LogSanitizer
import com.mulkkam.domain.logger.Logger
import org.koin.core.module.Module
import org.koin.dsl.module

fun loggingModule(
    isDebug: Boolean,
    firebaseBridge: FirebaseLoggingBridge,
): Module =
    module {
        single<LogSanitizer> { SensitiveInfoSanitizerImpl() }
        single<Logger> { LoggerImpl(sanitizer = get(), isDebug = isDebug, firebaseBridge = firebaseBridge) }
    }
