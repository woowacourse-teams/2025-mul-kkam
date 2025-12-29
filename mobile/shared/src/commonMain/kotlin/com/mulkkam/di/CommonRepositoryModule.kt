package com.mulkkam.di

import com.mulkkam.data.repository.AuthRepositoryImpl
import com.mulkkam.data.repository.CupsRepositoryImpl
import com.mulkkam.data.repository.DevicesRepositoryImpl
import com.mulkkam.data.repository.FriendsRepositoryImpl
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
import com.mulkkam.domain.repository.IntakeRepository
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.domain.repository.NicknameRepository
import com.mulkkam.domain.repository.NotificationRepository
import com.mulkkam.domain.repository.OnboardingRepository
import com.mulkkam.domain.repository.ReminderRepository
import com.mulkkam.domain.repository.TokenRepository
import com.mulkkam.domain.repository.VersionsRepository
import org.koin.dsl.module

val commonRepositoryModule =
    module {
        single<IntakeRepository> { IntakeRepositoryImpl(get()) }
        single<CupsRepository> { CupsRepositoryImpl(get()) }
        single<NicknameRepository> { NicknameRepositoryImpl(get()) }
        single<NotificationRepository> { NotificationRepositoryImpl(get()) }
        single<VersionsRepository> { VersionsRepositoryImpl(get()) }
        single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
        single<ReminderRepository> { ReminderRepositoryImpl(get()) }
        single<FriendsRepository> { FriendsRepositoryImpl(get()) }
        single<AuthRepository> { AuthRepositoryImpl(get()) }
        single<TokenRepository> { TokenRepositoryImpl(get()) }
        single<DevicesRepository> { DevicesRepositoryImpl(get(), get()) }
        single<MembersRepository> { MembersRepositoryImpl(get(), get()) }
    }
