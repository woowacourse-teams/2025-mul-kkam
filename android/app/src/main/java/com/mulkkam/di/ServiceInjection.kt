package com.mulkkam.di

import com.mulkkam.data.local.service.HealthService
import com.mulkkam.data.remote.service.AuthService
import com.mulkkam.data.remote.service.CupsService
import com.mulkkam.data.remote.service.IntakeService
import com.mulkkam.data.remote.service.MembersService
import com.mulkkam.data.remote.service.NicknameService
import com.mulkkam.data.remote.service.NotificationService
import com.mulkkam.di.HealthConnectInjection.healthConnectClient
import com.mulkkam.di.NetworkInjection.retrofit

object ServiceInjection {
    val intakeService: IntakeService = retrofit.create(IntakeService::class.java)

    val cupsService: CupsService = retrofit.create(CupsService::class.java)

    val authService: AuthService = retrofit.create(AuthService::class.java)

    val membersService: MembersService = retrofit.create(MembersService::class.java)

    val nicknameService: NicknameService = retrofit.create(NicknameService::class.java)

    val healthService: HealthService by lazy { HealthService(healthConnectClient) }

    val notificationService: NotificationService = retrofit.create(NotificationService::class.java)
}
