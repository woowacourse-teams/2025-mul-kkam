package com.mulkkam.di

import com.mulkkam.data.remote.datasource.CupsDataSource
import com.mulkkam.data.remote.datasource.CupsDataSourceImpl
import com.mulkkam.data.remote.datasource.FriendsDataSource
import com.mulkkam.data.remote.datasource.FriendsDataSourceImpl
import com.mulkkam.data.remote.datasource.IntakeDataSource
import com.mulkkam.data.remote.datasource.IntakeDataSourceImpl
import com.mulkkam.data.remote.datasource.NicknameDataSource
import com.mulkkam.data.remote.datasource.NicknameDataSourceImpl
import com.mulkkam.data.remote.datasource.NotificationDataSource
import com.mulkkam.data.remote.datasource.NotificationDataSourceImpl
import com.mulkkam.data.remote.datasource.OnboardingDataSource
import com.mulkkam.data.remote.datasource.OnboardingDataSourceImpl
import com.mulkkam.data.remote.datasource.ReminderDataSource
import com.mulkkam.data.remote.datasource.ReminderDataSourceImpl
import com.mulkkam.data.remote.datasource.VersionDataSource
import com.mulkkam.data.remote.datasource.VersionDataSourceImpl
import org.koin.dsl.module

val commonDataSourceModule =
    module {
        single<CupsDataSource> { CupsDataSourceImpl() }
        single<FriendsDataSource> { FriendsDataSourceImpl() }
        single<IntakeDataSource> { IntakeDataSourceImpl() }
        single<NicknameDataSource> { NicknameDataSourceImpl() }
        single<NotificationDataSource> { NotificationDataSourceImpl() }
        single<OnboardingDataSource> { OnboardingDataSourceImpl() }
        single<ReminderDataSource> { ReminderDataSourceImpl() }
        single<VersionDataSource> { VersionDataSourceImpl() }
    }
