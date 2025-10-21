package com.mulkkam.di

import androidx.health.connect.client.HealthConnectClient
import com.mulkkam.data.local.service.HealthService
import com.mulkkam.data.remote.service.AuthService
import com.mulkkam.data.remote.service.CupsService
import com.mulkkam.data.remote.service.DevicesService
import com.mulkkam.data.remote.service.FriendsService
import com.mulkkam.data.remote.service.IntakeService
import com.mulkkam.data.remote.service.MembersService
import com.mulkkam.data.remote.service.NicknameService
import com.mulkkam.data.remote.service.NotificationsService
import com.mulkkam.data.remote.service.OnboardingService
import com.mulkkam.data.remote.service.ReminderService
import com.mulkkam.data.remote.service.VersionsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideIntakeService(retrofit: Retrofit): IntakeService = retrofit.create(IntakeService::class.java)

    @Provides
    @Singleton
    fun provideCupsService(retrofit: Retrofit): CupsService = retrofit.create(CupsService::class.java)

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService = retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideMembersService(retrofit: Retrofit): MembersService = retrofit.create(MembersService::class.java)

    @Provides
    @Singleton
    fun provideNicknameService(retrofit: Retrofit): NicknameService = retrofit.create(NicknameService::class.java)

    @Provides
    @Singleton
    fun provideHealthService(client: HealthConnectClient): HealthService = HealthService(client)

    @Provides
    @Singleton
    fun provideDevicesService(retrofit: Retrofit): DevicesService = retrofit.create(DevicesService::class.java)

    @Provides
    @Singleton
    fun provideNotificationService(retrofit: Retrofit): NotificationsService = retrofit.create(NotificationsService::class.java)

    @Provides
    @Singleton
    fun provideVersionsService(retrofit: Retrofit): VersionsService = retrofit.create(VersionsService::class.java)

    @Provides
    @Singleton
    fun provideOnboardingService(retrofit: Retrofit): OnboardingService = retrofit.create(OnboardingService::class.java)

    @Provides
    @Singleton
    fun provideReminderService(retrofit: Retrofit): ReminderService = retrofit.create(ReminderService::class.java)

    @Provides
    @Singleton
    fun provideFriendsService(retrofit: Retrofit): FriendsService = retrofit.create(FriendsService::class.java)
}
