package com.seulseul.di

import okhttp3.OkHttpClient

object NetworkInjection {
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .build()
    }
}
