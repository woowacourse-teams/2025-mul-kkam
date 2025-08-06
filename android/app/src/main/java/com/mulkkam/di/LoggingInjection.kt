package com.mulkkam.di

import com.mulkkam.data.logger.LoggerImpl
import com.mulkkam.domain.logger.Logger

object LoggingInjection {
    val logger: Logger by lazy {
        LoggerImpl()
    }
}
