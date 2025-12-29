package com.mulkkam.util.logger

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mulkkam.domain.logger.LogSanitizer
import timber.log.Timber
import javax.inject.Inject

class ReleaseLoggingTree
    @Inject
    constructor(
        private val sanitizer: LogSanitizer,
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
