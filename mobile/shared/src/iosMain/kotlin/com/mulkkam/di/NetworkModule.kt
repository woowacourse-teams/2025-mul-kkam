package com.mulkkam.di

import com.mulkkam.data.local.datasource.TokenLocalDataSource
import com.mulkkam.data.remote.api.createHttpClient
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

fun networkModule(baseUrl: String): Module =
    module {
        single<HttpClient> {
            val tokenLocalDataSource: TokenLocalDataSource = get()
            createHttpClient(
                baseUrl = baseUrl,
                getAccessToken = { tokenLocalDataSource.accessToken },
                onUnauthorized = { null }, // TODO: 토큰 갱신 로직 구현
            )
        }
    }
