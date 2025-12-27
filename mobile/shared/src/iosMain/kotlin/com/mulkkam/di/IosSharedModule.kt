package com.mulkkam.di

import com.mulkkam.data.local.datasource.DevicesLocalDataSource
import com.mulkkam.data.local.datasource.DevicesLocalDataSourceImpl
import com.mulkkam.data.local.datasource.TokenLocalDataSource
import com.mulkkam.data.local.datasource.TokenLocalDataSourceImpl
import com.mulkkam.data.remote.api.createHttpClient
import com.mulkkam.data.remote.datasource.AuthRemoteDataSource
import com.mulkkam.data.remote.datasource.AuthRemoteDataSourceImpl
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

fun iosSharedModule(baseUrl: String): Module =
    module {
        // Local DataSources
        single<TokenLocalDataSource> { TokenLocalDataSourceImpl() }
        single<DevicesLocalDataSource> { DevicesLocalDataSourceImpl() }

        // HttpClient
        single<HttpClient> {
            val tokenDataSource: TokenLocalDataSource = get()
            createHttpClient(
                baseUrl = baseUrl,
                getAccessToken = { tokenDataSource.accessToken },
                onUnauthorized = { null }, // TODO: 토큰 갱신 로직 구현
            )
        }

        // Remote DataSources
        single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }

        // TODO: iOS용 Repository 구현 필요 (현재 Android에만 존재)
        // single<AuthRepository> { AuthRepositoryImpl(get()) }
        // single<TokenRepository> { TokenRepositoryImpl(get()) }
    }
