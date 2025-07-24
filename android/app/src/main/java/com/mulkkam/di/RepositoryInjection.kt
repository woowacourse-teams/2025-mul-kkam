package com.mulkkam.di

import com.mulkkam.data.repository.IntakeRepository

object RepositoryInjection {
    val intakeRepository: IntakeRepository by lazy {
        IntakeRepository(
            intakeService = NetworkInjection.intakeService,
        )
    }
}
