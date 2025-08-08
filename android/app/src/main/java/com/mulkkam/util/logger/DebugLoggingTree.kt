package com.mulkkam.util.logger

import com.mulkkam.domain.logger.SensitiveInfoSanitizer
import timber.log.Timber

class DebugLoggingTree(
    private val sanitizer: SensitiveInfoSanitizer,
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
