package com.mulkkam.di

import com.mulkkam.data.repository.AuthRepositoryImpl
import com.mulkkam.data.repository.CupsRepositoryImpl
import com.mulkkam.data.repository.DevicesRepositoryImpl
import com.mulkkam.data.repository.FriendsRepositoryImpl
import com.mulkkam.data.repository.HealthRepositoryImpl
import com.mulkkam.data.repository.IntakeRepositoryImpl
import com.mulkkam.data.repository.MembersRepositoryImpl
import com.mulkkam.data.repository.NicknameRepositoryImpl
import com.mulkkam.data.repository.NotificationRepositoryImpl
import com.mulkkam.data.repository.OnboardingRepositoryImpl
import com.mulkkam.data.repository.ReminderRepositoryImpl
import com.mulkkam.data.repository.TokenRepositoryImpl
import com.mulkkam.data.repository.VersionsRepositoryImpl
import com.mulkkam.domain.repository.AuthRepository
import com.mulkkam.domain.repository.CupsRepository
import com.mulkkam.domain.repository.DevicesRepository
import com.mulkkam.domain.repository.FriendsRepository
import com.mulkkam.domain.repository.HealthRepository
import com.mulkkam.domain.repository.IntakeRepository
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.domain.repository.NicknameRepository
import com.mulkkam.domain.repository.NotificationRepository
import com.mulkkam.domain.repository.OnboardingRepository
import com.mulkkam.domain.repository.ReminderRepository
import com.mulkkam.domain.repository.TokenRepository
import com.mulkkam.domain.repository.VersionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindIntakeRepository(impl: IntakeRepositoryImpl): IntakeRepository

    @Binds
    @Singleton
    abstract fun bindCupsRepository(impl: CupsRepositoryImpl): CupsRepository

    @Binds
    @Singleton
    abstract fun bindTokenRepository(impl: TokenRepositoryImpl): TokenRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindHealthRepository(impl: HealthRepositoryImpl): HealthRepository

    @Binds
    @Singleton
    abstract fun bindMembersRepository(impl: MembersRepositoryImpl): MembersRepository

    @Binds
    @Singleton
    abstract fun bindNicknameRepository(impl: NicknameRepositoryImpl): NicknameRepository

    @Binds
    @Singleton
    abstract fun bindDevicesRepository(impl: DevicesRepositoryImpl): DevicesRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindVersionsRepository(impl: VersionsRepositoryImpl): VersionsRepository

    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(impl: OnboardingRepositoryImpl): OnboardingRepository

    @Binds
    @Singleton
    abstract fun bindReminderRepository(impl: ReminderRepositoryImpl): ReminderRepository

    @Binds
    @Singleton
    abstract fun bindFriendsRepository(impl: FriendsRepositoryImpl): FriendsRepository
}
