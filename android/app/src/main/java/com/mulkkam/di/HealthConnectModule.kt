package com.mulkkam.di

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HealthConnectModule {
    const val PROVIDER_PACKAGE_NAME: String = "com.google.android.apps.healthdata"

    @Provides
    @Singleton
    fun provideHealthConnectClient(
        @ApplicationContext context: Context,
    ): HealthConnectClient =
        runCatching {
            HealthConnectClient.getOrCreate(context)
        }.getOrNull() ?: error("")
}
