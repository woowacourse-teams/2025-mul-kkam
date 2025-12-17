package com.mulkkam.di

import com.mulkkam.BuildConfig
import com.mulkkam.data.logger.LoggerImpl
import com.mulkkam.data.logger.SensitiveInfoSanitizerImpl
import com.mulkkam.domain.logger.LogSanitizer
import com.mulkkam.domain.logger.Logger
import com.mulkkam.util.logger.DebugLoggingTree
import com.mulkkam.util.logger.ReleaseLoggingTree
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoggingModule {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ReleaseTree

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DebugTree

    @Provides
    @Singleton
    fun provideSanitizer(): LogSanitizer = SensitiveInfoSanitizerImpl()

    @Provides
    @Singleton
    fun provideLogger(sanitizer: LogSanitizer): Logger = LoggerImpl(sanitizer)

    @Provides
    @Singleton
    @ReleaseTree
    fun provideReleaseTimberTree(sanitizer: LogSanitizer): Timber.Tree = ReleaseLoggingTree(sanitizer)

    @Provides
    @Singleton
    @DebugTree
    fun provideDebugTimberTree(sanitizer: LogSanitizer): Timber.Tree = DebugLoggingTree(sanitizer)

    @Provides
    @Singleton
    fun provideTimberTree(
        @ReleaseTree releaseTree: Timber.Tree,
        @DebugTree debugTree: Timber.Tree,
        isDebug: Boolean = BuildConfig.DEBUG,
    ): Timber.Tree = if (isDebug) debugTree else releaseTree
}
