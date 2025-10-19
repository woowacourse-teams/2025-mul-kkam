package com.mulkkam.di

import com.mulkkam.data.logger.LoggerImpl
import com.mulkkam.data.logger.SensitiveInfoSanitizerImpl
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.logger.SensitiveInfoSanitizer
import com.mulkkam.util.logger.DebugLoggingTree
import com.mulkkam.util.logger.ReleaseLoggingTree
import timber.log.Timber

object LoggingInjection {
    val mulKkamLogger: Logger by lazy {
        LoggerImpl(sanitizer)
    }

    private val sanitizer: SensitiveInfoSanitizer by lazy {
        SensitiveInfoSanitizerImpl()
    }

    private val releaseTimberTree: Timber.Tree by lazy {
        ReleaseLoggingTree(sanitizer)
    }

    private val debugTimberTree: Timber.Tree by lazy {
        DebugLoggingTree(sanitizer)
    }

    fun init(
        userId: String?,
        isDebug: Boolean,
    ) {
        mulKkamLogger.init(userId)

        val timberTree = if (isDebug) debugTimberTree else releaseTimberTree
        Timber.plant(timberTree)
    }
}
