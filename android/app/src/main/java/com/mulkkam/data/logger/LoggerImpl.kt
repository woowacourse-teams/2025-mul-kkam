package com.mulkkam.data.logger

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mulkkam.BuildConfig
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.logger.SensitiveInfoSanitizer
import com.mulkkam.domain.model.LogEntry
import com.mulkkam.domain.model.LogLevel
import timber.log.Timber

class LoggerImpl(
    private val sanitizer: SensitiveInfoSanitizer,
) : Logger {
    override fun log(entry: LogEntry) {
        val rawMessage =
            buildString {
                append("level: ${entry.level}, ")
                append("event: ${entry.event}, ")
                append(entry.message)
                append(" | userId = ${entry.userId}")
            }

        val safeMessage = sanitizer.sanitize(rawMessage)
        val safePayloadMessage = sanitizer.sanitize(entry.message)

        when (entry.level) {
            LogLevel.ERROR -> Timber.tag(DEFAULT_LOGGING_TAG).e(safeMessage)
            LogLevel.WARN -> Timber.tag(DEFAULT_LOGGING_TAG).w(safeMessage)
            LogLevel.INFO -> Timber.tag(DEFAULT_LOGGING_TAG).i(safeMessage)
            LogLevel.DEBUG -> Timber.tag(DEFAULT_LOGGING_TAG).d(safeMessage)
        }

        if (!BuildConfig.DEBUG) {
            Firebase.analytics.logEvent(
                entry.event.name,
                Bundle().apply {
                    putString("level", entry.level.name)
                    putString("message", safePayloadMessage)
                    entry.userId?.let { putString("userId", it) }
                },
            )

            if (entry.level == LogLevel.ERROR) {
                FirebaseCrashlytics.getInstance().recordException(Exception(safeMessage))
            }
        }
    }

    companion object {
        private const val DEFAULT_LOGGING_TAG = "[MULKKAM_LOGGER]"
    }
}
