package com.mulkkam.util.logger

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mulkkam.domain.logger.SensitiveInfoSanitizer
import timber.log.Timber

class ReleaseLoggingTree(
    private val sanitizer: SensitiveInfoSanitizer,
) : Timber.Tree() {
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
    ) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) return

        val safeMessage = sanitizer.sanitize(message)
        FirebaseCrashlytics.getInstance().log(safeMessage)

        if (priority == Log.ERROR && t != null) {
            FirebaseCrashlytics.getInstance().recordException(t)
        }
    }
}
