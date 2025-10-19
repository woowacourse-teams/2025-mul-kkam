package com.mulkkam.data.logger

import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mulkkam.BuildConfig
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.logger.SensitiveInfoSanitizer
import com.mulkkam.domain.model.logger.LogEntry
import com.mulkkam.domain.model.logger.LogLevel
import timber.log.Timber

class LoggerImpl(
    private val sanitizer: SensitiveInfoSanitizer,
    private val analytics: FirebaseAnalytics = Firebase.analytics,
    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance(),
    private val isDebug: Boolean = BuildConfig.DEBUG,
) : Logger {
    private var userId: String? = null

    override fun init(userId: String?) {
        this.userId = userId
    }

    override fun log(entry: LogEntry) {
        val rawMessage = formatMessage(entry.copy(userId = userId))
        val safeMessage = sanitizer.sanitize(rawMessage)
        val safePayloadMessage = sanitizer.sanitize(entry.message)

        logToTimber(entry.level, DEFAULT_LOGGING_TAG, safeMessage)

        if (isDebug) return

        logToAnalytics(analytics, entry, safePayloadMessage)

        if (entry.level == LogLevel.ERROR) {
            crashlytics.recordException(Exception(safeMessage))
        }
    }

    private fun formatMessage(entry: LogEntry): String =
        buildString {
            append("level=").append(entry.level)
            append(", event=").appendLine(entry.event)
            append("timestamp=").appendLine(entry.timestamp)
            append("message=").append(entry.message)
            append(" | userId=").append(entry.userId)
        }

    private fun logToTimber(
        level: LogLevel,
        tag: String,
        message: String,
    ) {
        val timber = Timber.tag(tag)
        when (level) {
            LogLevel.ERROR -> timber.e(message)
            LogLevel.WARN -> timber.w(message)
            LogLevel.INFO -> timber.i(message)
            LogLevel.DEBUG -> timber.d(message)
        }
    }

    private fun logToAnalytics(
        analytics: FirebaseAnalytics,
        entry: LogEntry,
        safePayloadMessage: String,
    ) {
        val params: Bundle =
            bundleOf(
                PARAM_LEVEL to entry.level.name,
                PARAM_MESSAGE to safePayloadMessage,
            ).apply {
                entry.userId?.let { putString(PARAM_USER_ID, it) }
            }

        analytics.logEvent(entry.event.name, params)
    }

    companion object {
        private const val DEFAULT_LOGGING_TAG = "[MULKKAM_LOGGER]"

        private const val PARAM_LEVEL = "level"
        private const val PARAM_MESSAGE = "message"
        private const val PARAM_USER_ID = "userId"
    }
}
