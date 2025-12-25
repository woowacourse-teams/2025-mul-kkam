package com.mulkkam.di

import com.mulkkam.data.local.datasource.DevicesLocalDataSource
import com.mulkkam.data.local.datasource.DevicesLocalDataSourceImpl
import com.mulkkam.data.local.datasource.TokenLocalDataSource
import com.mulkkam.data.local.datasource.TokenLocalDataSourceImpl
import com.mulkkam.data.remote.api.createHttpClient
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

fun androidSharedModule(baseUrl: String): Module =
    module {
        // Local DataSources
        single<TokenLocalDataSource> { TokenLocalDataSourceImpl(androidContext()) }
        single<DevicesLocalDataSource> { DevicesLocalDataSourceImpl(androidContext()) }

        // HttpClient
        single<HttpClient> {
            val tokenDataSource: TokenLocalDataSource = get()
            createHttpClient(
                baseUrl = baseUrl,
                getAccessToken = { tokenDataSource.accessToken },
                onUnauthorized = { null }, // TODO: 토큰 갱신 로직 구현
            )
        }

        includes(sharedModule)
    }
