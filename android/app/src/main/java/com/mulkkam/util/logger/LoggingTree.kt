package com.mulkkam.util.logger

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class LoggingTree : Timber.Tree() {
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
    ) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) return

        FirebaseCrashlytics.getInstance().log(message)

        if (priority == Log.ERROR && t != null) {
            FirebaseCrashlytics.getInstance().recordException(t)
        }
    }
}
