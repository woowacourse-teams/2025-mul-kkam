package com.mulkkam.di

import com.mulkkam.data.logger.LoggerImpl
import com.mulkkam.data.logger.SensitiveInfoSanitizerImpl
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.logger.SensitiveInfoSanitizer
import com.mulkkam.util.logger.DebugLoggingTree
import com.mulkkam.util.logger.ReleaseLoggingTree
import timber.log.Timber

object LoggingInjection {
    private val sanitizer: SensitiveInfoSanitizer by lazy {
        SensitiveInfoSanitizerImpl()
    }

    val mulKkamLogger: Logger by lazy {
        LoggerImpl(sanitizer)
    }

    val releaseTimberTree: Timber.Tree by lazy {
        ReleaseLoggingTree(sanitizer)
    }

    val debugTimberTree: Timber.Tree by lazy {
        DebugLoggingTree(sanitizer)
    }
}
