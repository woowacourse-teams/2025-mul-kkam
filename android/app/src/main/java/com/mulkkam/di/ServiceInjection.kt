package com.mulkkam.di

import com.mulkkam.data.local.service.HealthService
import com.mulkkam.data.remote.service.AuthService
import com.mulkkam.data.remote.service.CupsService
import com.mulkkam.data.remote.service.DevicesService
import com.mulkkam.data.remote.service.IntakeService
import com.mulkkam.data.remote.service.MembersService
import com.mulkkam.data.remote.service.NicknameService
import com.mulkkam.data.remote.service.NotificationsService
import com.mulkkam.di.HealthConnectInjection.healthConnectClient
import com.mulkkam.di.NetworkInjection.retrofit

object ServiceInjection {
    val intakeService: IntakeService by lazy {
        retrofit.create(IntakeService::class.java)
    }

    val cupsService: CupsService by lazy {
        retrofit.create(CupsService::class.java)
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val membersService: MembersService by lazy {
        retrofit.create(MembersService::class.java)
    }

    val nicknameService: NicknameService by lazy {
        retrofit.create(NicknameService::class.java)
    }

    val healthService: HealthService by lazy {
        HealthService(healthConnectClient)
    }

    val devicesService: DevicesService by lazy {
        retrofit.create(DevicesService::class.java)
    }

    val notificationService: NotificationsService by lazy {
        retrofit.create(NotificationsService::class.java)
    }
}
