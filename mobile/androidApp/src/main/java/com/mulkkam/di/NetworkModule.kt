package com.mulkkam.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mulkkam.BuildConfig
import com.mulkkam.data.remote.adapter.MulKkamCallAdapterFactory
import com.mulkkam.data.remote.interceptor.AuthorizationInterceptor
import com.mulkkam.data.remote.interceptor.NetworkLoggingInterceptor
import com.mulkkam.data.remote.interceptor.TokenRefresher
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit

val networkModule =
    module {
        single {
            OkHttpClient()
                .newBuilder()
                .addInterceptor(AuthorizationInterceptor(get(), lazy { get() }))
                .addInterceptor(NetworkLoggingInterceptor(get()))
                .build()
        }

        single<Retrofit> {
            Retrofit
                .Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(get())
                .addCallAdapterFactory(MulKkamCallAdapterFactory())
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .build()
        }

        single {
            TokenRefresher(get(), get(), get())
        }
    }
