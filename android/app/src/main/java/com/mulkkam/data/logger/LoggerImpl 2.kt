package com.mulkkam.data.logger

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.LogEntry
import com.mulkkam.domain.model.LogLevel
import timber.log.Timber

class LoggerImpl : Logger {
    override fun log(entry: LogEntry) {
        val logMessage =
            buildString {
                append("level: ${entry.level}, ")
                append("event: ${entry.event}, ")
                append(entry.message)
                append(" | userId = ${entry.userId}")
            }

        when (entry.level) {
            LogLevel.ERROR -> Timber.tag(DEFAULT_LOGGING_TAG).e(logMessage)
            LogLevel.WARN -> Timber.tag(DEFAULT_LOGGING_TAG).w(logMessage)
            LogLevel.INFO -> Timber.tag(DEFAULT_LOGGING_TAG).i(logMessage)
            LogLevel.DEBUG -> Timber.tag(DEFAULT_LOGGING_TAG).d(logMessage)
        }

        Firebase.analytics.logEvent(
            entry.event.name,
            Bundle().apply {
                putString("level", entry.level.name)
                putString("message", entry.message)
                entry.userId?.let { putString("userId", it) }
            },
        )

        if (entry.level == LogLevel.ERROR) {
            FirebaseCrashlytics.getInstance().recordException(Exception(logMessage))
        }
    }

    companion object {
        private const val DEFAULT_LOGGING_TAG = "[MULKKAM_LOGGER]"
    }
}
