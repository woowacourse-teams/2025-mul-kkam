package com.mulkkam.di

import com.mulkkam.domain.logger.FirebaseLoggingBridge
import org.koin.core.context.startKoin

fun initKoin(
    baseUrl: String,
    isDebug: Boolean,
    firebaseBridge: FirebaseLoggingBridge,
) {
    startKoin {
        modules(iosSharedModule(baseUrl, isDebug, firebaseBridge))
    }
}
