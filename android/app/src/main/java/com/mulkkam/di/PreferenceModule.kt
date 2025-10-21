package com.mulkkam.di

import android.content.Context
import com.mulkkam.data.local.preference.DevicesPreference
import com.mulkkam.data.local.preference.MembersPreference
import com.mulkkam.data.local.preference.TokenPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {
    @Provides
    @Singleton
    fun provideTokenPreference(
        @ApplicationContext context: Context,
    ): TokenPreference = TokenPreference(context)

    @Provides
    @Singleton
    fun provideMembersPreference(
        @ApplicationContext context: Context,
    ): MembersPreference = MembersPreference(context)

    @Provides
    @Singleton
    fun provideDevicesPreference(
        @ApplicationContext context: Context,
    ): DevicesPreference = DevicesPreference(context)
}
