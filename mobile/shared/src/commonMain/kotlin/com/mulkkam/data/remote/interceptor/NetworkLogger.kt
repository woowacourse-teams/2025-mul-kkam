package com.mulkkam.data.remote.interceptor

import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import io.ktor.client.plugins.logging.Logger as KtorLogger

class NetworkLogger(
    private val logger: Logger,
) : KtorLogger {
    override fun log(message: String) {
        when {
            message.contains("REQUEST:", ignoreCase = true) -> {
                logger.debug(
                    event = LogEvent.NETWORK,
                    message = "\n🏹️ $message",
                )
            }

            message.contains("RESPONSE:", ignoreCase = true) -> {
                logger.debug(
                    event = LogEvent.NETWORK,
                    message = "\n🛡️️ $message",
                )
            }

            message.contains("BODY START", ignoreCase = true) -> {
                logger.debug(
                    event = LogEvent.NETWORK,
                    message = "Body: $message",
                )
            }

            message.contains("BODY END", ignoreCase = true) -> {
                Unit
            }

            else -> {
                logger.debug(
                    event = LogEvent.NETWORK,
                    message = message,
                )
            }
        }
    }
}
