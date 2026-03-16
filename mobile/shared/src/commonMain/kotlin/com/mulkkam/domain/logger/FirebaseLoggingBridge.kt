package com.mulkkam.domain.logger

interface FirebaseLoggingBridge {
    fun log(
        eventName: String,
        level: String,
        message: String,
        userId: String?,
    )

    fun recordException(message: String)
}
