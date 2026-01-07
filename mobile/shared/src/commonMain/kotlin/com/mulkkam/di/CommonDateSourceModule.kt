package com.mulkkam.di

import com.mulkkam.data.remote.datasource.CupsRemoteDataSource
import com.mulkkam.data.remote.datasource.CupsRemoteDataSourceImpl
import com.mulkkam.data.remote.datasource.DevicesRemoteDataSource
import com.mulkkam.data.remote.datasource.DevicesRemoteDataSourceImpl
import com.mulkkam.data.remote.datasource.FriendsRemoteDataSource
import com.mulkkam.data.remote.datasource.FriendsRemoteDataSourceImpl
import com.mulkkam.data.remote.datasource.IntakeRemoteDataSource
import com.mulkkam.data.remote.datasource.IntakeRemoteDataSourceImpl
import com.mulkkam.data.remote.datasource.MembersRemoteDataSource
import com.mulkkam.data.remote.datasource.MembersRemoteDataSourceImpl
import com.mulkkam.data.remote.datasource.NicknameRemoteDataSource
import com.mulkkam.data.remote.datasource.NicknameRemoteDataSourceImpl
import com.mulkkam.data.remote.datasource.NotificationRemoteDataSource
import com.mulkkam.data.remote.datasource.NotificationRemoteDataSourceImpl
import com.mulkkam.data.remote.datasource.OnboardingRemoteDataSource
import com.mulkkam.data.remote.datasource.OnboardingRemoteDataSourceImpl
import com.mulkkam.data.remote.datasource.ReminderRemoteDataSource
import com.mulkkam.data.remote.datasource.ReminderRemoteDataSourceImpl
import com.mulkkam.data.remote.datasource.VersionRemoteDataSource
import com.mulkkam.data.remote.datasource.VersionRemoteDataSourceImpl
import org.koin.dsl.module

val commonDataSourceModule =
    module {
        single<CupsRemoteDataSource> { CupsRemoteDataSourceImpl() }
        single<DevicesRemoteDataSource> { DevicesRemoteDataSourceImpl(get()) }
        single<FriendsRemoteDataSource> { FriendsRemoteDataSourceImpl() }
        single<IntakeRemoteDataSource> { IntakeRemoteDataSourceImpl(get()) }
        single<MembersRemoteDataSource> { MembersRemoteDataSourceImpl() }
        single<NicknameRemoteDataSource> { NicknameRemoteDataSourceImpl() }
        single<NotificationRemoteDataSource> { NotificationRemoteDataSourceImpl() }
        single<OnboardingRemoteDataSource> { OnboardingRemoteDataSourceImpl(get()) }
        single<ReminderRemoteDataSource> { ReminderRemoteDataSourceImpl() }
        single<VersionRemoteDataSource> { VersionRemoteDataSourceImpl(get()) }
    }
