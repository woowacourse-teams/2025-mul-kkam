package com.mulkkam.di

import com.mulkkam.data.local.datasource.DevicesLocalDataSource
import com.mulkkam.data.local.datasource.DevicesLocalDataSourceImpl
import com.mulkkam.data.local.datasource.TokenLocalDataSource
import com.mulkkam.data.local.datasource.TokenLocalDataSourceImpl
import com.mulkkam.data.logger.LoggerImpl
import com.mulkkam.data.logger.LoggerInitializer
import com.mulkkam.data.logger.SensitiveInfoSanitizerImpl
import com.mulkkam.data.remote.api.createHttpClient
import com.mulkkam.data.remote.interceptor.TokenRefresher
import com.mulkkam.domain.logger.LogSanitizer
import com.mulkkam.domain.logger.Logger
import com.mulkkam.util.logger.DebugLoggingTree
import com.mulkkam.util.logger.ReleaseLoggingTree
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber

fun androidSharedModule(
    baseUrl: String,
    isDebug: Boolean,
): Module =
    module {
        // Local DataSources
        single<TokenLocalDataSource> { TokenLocalDataSourceImpl(androidContext()) }
        single<DevicesLocalDataSource> { DevicesLocalDataSourceImpl(androidContext()) }

        // HttpClient
        single<HttpClient> {
            val tokenDataSource: TokenLocalDataSource = get()
            createHttpClient(
                baseUrl = baseUrl,
                getAccessToken = { tokenDataSource.accessToken },
                onUnauthorized = { null }, // TODO: 토큰 갱신 로직 구현
            )
        }

        // LoggingModule
        single<LogSanitizer> { SensitiveInfoSanitizerImpl() }
        single<Logger> { LoggerImpl(sanitizer = get(), isDebug = isDebug) }

        single(named("release")) { ReleaseLoggingTree(get()) }
        single(named("debug")) { DebugLoggingTree(get()) }

        single<Timber.Tree> {
            if (isDebug) {
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

        single {
            TokenRefresher(get(), get(), get())
        }

        // TODO:
        includes(
            checkerModule2,
            healthConnectModule2,
            viewModelModule2,
            preferenceModule2,
            repositoryModule2,
            serviceModule2,
            sharedModule,
            workerModule2,
            workManagerModule2,
        )
    }
