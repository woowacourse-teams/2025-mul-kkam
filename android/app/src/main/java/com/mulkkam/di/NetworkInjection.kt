package com.mulkkam.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mulkkam.BuildConfig
import com.mulkkam.data.remote.adapter.MulKkamCallAdapterFactory
import com.mulkkam.data.remote.interceptor.AuthorizationInterceptor
import com.mulkkam.data.remote.interceptor.NetworkLoggingInterceptor
import com.mulkkam.di.LoggingInjection.mulKkamLogger
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object NetworkInjection {
    private val contentType = "application/json".toMediaType()

    private val interceptorClient by lazy {
        OkHttpClient()
            .newBuilder()
            .addInterceptor(AuthorizationInterceptor())
            .addInterceptor(NetworkLoggingInterceptor(mulKkamLogger))
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(interceptorClient)
            .addCallAdapterFactory(MulKkamCallAdapterFactory())
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }
}
