package com.mulkkam.di

import com.mulkkam.data.repository.AuthRepository
import com.mulkkam.data.repository.CupsRepository
import com.mulkkam.data.repository.HealthRepositoryImpl
import com.mulkkam.data.repository.IntakeRepository
import com.mulkkam.data.repository.TokenRepository
import com.mulkkam.di.PreferenceInjection.tokenPreference
import com.mulkkam.di.ServiceInjection.authService
import com.mulkkam.di.ServiceInjection.cupsService
import com.mulkkam.di.ServiceInjection.healthService
import com.mulkkam.di.ServiceInjection.intakeService
import com.mulkkam.domain.repository.HealthRepository

object RepositoryInjection {
    val intakeRepository: IntakeRepository by lazy {
        IntakeRepository(intakeService)
    }

    val cupsRepository: CupsRepository by lazy {
        CupsRepository(cupsService)
    }

    val tokenRepository: TokenRepository by lazy {
        TokenRepository(tokenPreference)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(authService)
    }

    val healthRepository: HealthRepository by lazy {
        HealthRepositoryImpl(healthService)
    }
}
