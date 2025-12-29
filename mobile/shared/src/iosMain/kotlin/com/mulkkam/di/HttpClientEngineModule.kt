package com.mulkkam.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

val httpClientEngineModule: Module =
    module {
        single<HttpClientEngine> { Darwin.create() }
    }
