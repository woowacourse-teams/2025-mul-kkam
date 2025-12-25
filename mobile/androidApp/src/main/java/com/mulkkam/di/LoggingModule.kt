package com.mulkkam.di

import com.mulkkam.BuildConfig
import com.mulkkam.data.logger.LoggerImpl
import com.mulkkam.data.logger.LoggerInitializer
import com.mulkkam.data.logger.SensitiveInfoSanitizerImpl
import com.mulkkam.domain.logger.LogSanitizer
import com.mulkkam.domain.logger.Logger
import com.mulkkam.util.logger.DebugLoggingTree
import com.mulkkam.util.logger.ReleaseLoggingTree
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber

val loggingModule =
    module {
        single<LogSanitizer> { SensitiveInfoSanitizerImpl() }
        single<Logger> { LoggerImpl(sanitizer = get(), isDebug = BuildConfig.DEBUG) }

        single(named("release")) { ReleaseLoggingTree(get()) }
        single(named("debug")) { DebugLoggingTree(get()) }

        single<Timber.Tree> {
            if (BuildConfig.DEBUG) {
                get(named("debug"))
            } else {
                get(named("release"))
            }
        }
        single {
            LoggerInitializer(
                get(),
                debugLoggingTree = get(named("debug")),
                releaseLoggingTree = get(named("release")),
            )
        }
    }
