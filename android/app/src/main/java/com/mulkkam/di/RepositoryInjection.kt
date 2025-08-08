package com.mulkkam.di

import com.mulkkam.data.repository.AuthRepositoryImpl
import com.mulkkam.data.repository.CupsRepositoryImpl
import com.mulkkam.data.repository.DevicesRepositoryImpl
import com.mulkkam.data.repository.HealthRepositoryImpl
import com.mulkkam.data.repository.IntakeRepositoryImpl
import com.mulkkam.data.repository.MembersRepositoryImpl
import com.mulkkam.data.repository.NicknameRepositoryImpl
import com.mulkkam.data.repository.NotificationRepositoryImpl
import com.mulkkam.data.repository.TokenRepositoryImpl
import com.mulkkam.di.PreferenceInjection.tokenPreference
import com.mulkkam.di.ServiceInjection.authService
import com.mulkkam.di.ServiceInjection.cupsService
import com.mulkkam.di.ServiceInjection.devicesService
import com.mulkkam.di.ServiceInjection.healthService
import com.mulkkam.di.ServiceInjection.intakeService
import com.mulkkam.di.ServiceInjection.membersService
import com.mulkkam.di.ServiceInjection.nicknameService
import com.mulkkam.di.ServiceInjection.notificationService
import com.mulkkam.domain.repository.AuthRepository
import com.mulkkam.domain.repository.CupsRepository
import com.mulkkam.domain.repository.DevicesRepository
import com.mulkkam.domain.repository.HealthRepository
import com.mulkkam.domain.repository.IntakeRepository
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.domain.repository.NicknameRepository
import com.mulkkam.domain.repository.NotificationRepository
import com.mulkkam.domain.repository.TokenRepository

object RepositoryInjection {
    val intakeRepository: IntakeRepository = IntakeRepositoryImpl(intakeService)

    val cupsRepository: CupsRepository = CupsRepositoryImpl(cupsService)

    val tokenRepository: TokenRepository = TokenRepositoryImpl(tokenPreference)

    val authRepository: AuthRepository = AuthRepositoryImpl(authService)

    val healthRepository: HealthRepository = HealthRepositoryImpl(healthService)

    val membersRepository: MembersRepository = MembersRepositoryImpl(membersService)

    val nicknameRepository: NicknameRepository = NicknameRepositoryImpl(nicknameService)

    val devicesRepository: DevicesRepository = DevicesRepositoryImpl(devicesService)

    val notificationRepository: NotificationRepository = NotificationRepositoryImpl(notificationService)
}
