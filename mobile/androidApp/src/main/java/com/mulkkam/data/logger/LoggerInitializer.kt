package com.mulkkam.data.logger

import com.mulkkam.domain.logger.Logger
import com.mulkkam.util.logger.DebugLoggingTree
import com.mulkkam.util.logger.ReleaseLoggingTree
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggerInitializer
    @Inject
    constructor(
        private val logger: Logger,
        private val debugLoggingTree: DebugLoggingTree,
        private val releaseLoggingTree: ReleaseLoggingTree,
    ) {
        fun initialize(
            userId: String?,
            isDebug: Boolean,
        ) {
            logger.init(userId)
            val tree = if (isDebug) debugLoggingTree else releaseLoggingTree
            if (Timber.forest().any { it === tree }.not()) {
                Timber.plant(tree)
            }
        }
    }
