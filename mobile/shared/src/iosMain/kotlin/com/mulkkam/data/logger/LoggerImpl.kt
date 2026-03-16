package com.mulkkam.data.logger

import com.mulkkam.domain.logger.FirebaseLoggingBridge
import com.mulkkam.domain.logger.LogSanitizer
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEntry
import com.mulkkam.domain.model.logger.LogLevel
import platform.Foundation.NSLog

class LoggerImpl(
    private val sanitizer: LogSanitizer,
    private val isDebug: Boolean,
    private val firebaseBridge: FirebaseLoggingBridge,
) : Logger {
    private var userId: String? = null

    override fun init(userId: String?) {
        this.userId = userId
    }

    override fun log(entry: LogEntry) {
        val rawMessage = formatMessage(entry.copy(userId = userId))
        val safeMessage = sanitizer.sanitize(rawMessage)
        val safePayloadMessage = sanitizer.sanitize(entry.message)

        logToNSLog(entry.level, safeMessage)

        if (isDebug) return

        firebaseBridge.log(
            eventName = entry.event.name,
            level = entry.level.name,
            message = safePayloadMessage,
            userId = userId,
        )

        if (entry.level == LogLevel.ERROR) {
            firebaseBridge.recordException(safeMessage)
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

    private fun logToNSLog(
        level: LogLevel,
        message: String,
    ) {
        val prefix =
            when (level) {
                LogLevel.ERROR -> "❌ ERROR"
                LogLevel.WARN -> "⚠️ WARN"
                LogLevel.INFO -> "ℹ️ INFO"
                LogLevel.DEBUG -> "🐛 DEBUG"
            }
        NSLog("[$DEFAULT_LOGGING_TAG][$prefix] $message")
    }

    companion object {
        private const val DEFAULT_LOGGING_TAG = "MULKKAM_LOGGER"
    }
}
