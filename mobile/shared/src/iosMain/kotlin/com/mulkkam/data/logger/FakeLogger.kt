package com.mulkkam.data.logger

import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEntry

// TODO: iOS용 실제 Logger 구현 필요

class FakeLogger : Logger {
    override fun init(userId: String?) {
        // No-op
    }

    override fun log(entry: LogEntry) {
        // No-op
    }
}
