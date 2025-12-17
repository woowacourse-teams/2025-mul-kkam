package com.mulkkam.util.logger

import com.mulkkam.domain.logger.LogSanitizer
import timber.log.Timber
import javax.inject.Inject

class DebugLoggingTree
    @Inject
    constructor(
        private val sanitizer: LogSanitizer,
    ) : Timber.DebugTree() {
        override fun log(
            priority: Int,
            tag: String?,
            message: String,
            t: Throwable?,
        ) {
            val safeMessage = sanitizer.sanitize(message)
            super.log(priority, tag, safeMessage, t)
        }
    }
