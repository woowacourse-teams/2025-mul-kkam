package com.mulkkam.di

import org.koin.core.context.startKoin

fun initKoin(
    baseUrl: String,
    isDebug: Boolean,
) {
    startKoin {
        modules(iosSharedModule(baseUrl, isDebug))
    }
}
