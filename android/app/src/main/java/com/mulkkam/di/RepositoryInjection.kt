package com.mulkkam.di

import com.mulkkam.data.repository.AuthRepositoryImpl
import com.mulkkam.data.repository.CupsRepositoryImpl
import com.mulkkam.data.repository.IntakeRepositoryImpl
import com.mulkkam.data.repository.TokenRepositoryImpl
import com.mulkkam.di.NetworkInjection.authService
import com.mulkkam.di.NetworkInjection.cupsService
import com.mulkkam.di.NetworkInjection.intakeService
import com.mulkkam.di.PreferenceInjection.tokenPreference
import com.mulkkam.domain.repository.AuthRepository
import com.mulkkam.domain.repository.CupsRepository
import com.mulkkam.domain.repository.IntakeRepository
import com.mulkkam.domain.repository.TokenRepository

object RepositoryInjection {
    val intakeRepository: IntakeRepository by lazy {
        IntakeRepositoryImpl(intakeService)
    }

    val cupsRepository: CupsRepository by lazy {
        CupsRepositoryImpl(cupsService)
    }

    val tokenRepository: TokenRepository by lazy {
        TokenRepositoryImpl(tokenPreference)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authService)
    }
}
