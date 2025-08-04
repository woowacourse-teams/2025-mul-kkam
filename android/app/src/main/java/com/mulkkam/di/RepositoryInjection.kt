package com.mulkkam.di

import com.mulkkam.data.repository.AuthRepository
import com.mulkkam.data.repository.CupsRepository
import com.mulkkam.data.repository.IntakeRepository
import com.mulkkam.data.repository.TokenRepository
import com.mulkkam.di.NetworkInjection.authService
import com.mulkkam.di.NetworkInjection.cupsService
import com.mulkkam.di.NetworkInjection.intakeService
import com.mulkkam.di.PreferenceInjection.tokenPreference

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
}
