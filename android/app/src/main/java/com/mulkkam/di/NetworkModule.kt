package com.mulkkam.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mulkkam.BuildConfig
import com.mulkkam.data.local.preference.TokenPreference
import com.mulkkam.data.remote.adapter.MulKkamCallAdapterFactory
import com.mulkkam.data.remote.interceptor.AuthorizationInterceptor
import com.mulkkam.data.remote.interceptor.NetworkLoggingInterceptor
import com.mulkkam.data.remote.interceptor.TokenRefresher
import com.mulkkam.domain.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideInterceptorClient(
        logger: Logger,
        tokenPreference: TokenPreference,
        tokenRefresher: TokenRefresher,
    ) = OkHttpClient()
        .newBuilder()
        .addInterceptor(AuthorizationInterceptor(tokenPreference, tokenRefresher))
        .addInterceptor(NetworkLoggingInterceptor(logger))
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(interceptorClient: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(interceptorClient)
            .addCallAdapterFactory(MulKkamCallAdapterFactory())
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
}
