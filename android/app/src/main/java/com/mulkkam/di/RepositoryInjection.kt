package com.mulkkam.di

import com.mulkkam.data.repository.CupsRepository
import com.mulkkam.data.repository.IntakeRepository

object RepositoryInjection {
    val intakeRepository: IntakeRepository by lazy {
        IntakeRepository(
            intakeService = NetworkInjection.intakeService,
        )
    }

    val cupsRepository: CupsRepository by lazy {
        CupsRepository(
            cupsService = NetworkInjection.cupsService,
        )
    }
}
