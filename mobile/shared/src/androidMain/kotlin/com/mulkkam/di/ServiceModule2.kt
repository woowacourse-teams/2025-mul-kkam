package com.mulkkam.di

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
import org.koin.dsl.module
import retrofit2.Retrofit

val serviceModule2 =
    module {
        single { get<Retrofit>().create(IntakeService::class.java) }
        single { get<Retrofit>().create(CupsService::class.java) }
        single { get<Retrofit>().create(AuthService::class.java) }
        single { get<Retrofit>().create(MembersService::class.java) }
        single { get<Retrofit>().create(NicknameService::class.java) }
        single { get<Retrofit>().create(DevicesService::class.java) }
        single { get<Retrofit>().create(NotificationsService::class.java) }
        single { get<Retrofit>().create(VersionsService::class.java) }
        single { get<Retrofit>().create(OnboardingService::class.java) }
        single { get<Retrofit>().create(ReminderService::class.java) }
        single { get<Retrofit>().create(FriendsService::class.java) }
        single { HealthService(get()) }
    }
