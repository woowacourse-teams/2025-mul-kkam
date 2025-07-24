package com.mulkkam.di

import com.mulkkam.data.repository.CupsRepository
import com.mulkkam.data.repository.IntakeRepository
import com.mulkkam.di.NetworkInjection.cupsService
import com.mulkkam.di.NetworkInjection.intakeService

object RepositoryInjection {
    val intakeRepository: IntakeRepository by lazy {
        IntakeRepository(intakeService)
    }

    val cupsRepository: CupsRepository by lazy {
        CupsRepository(cupsService)
    }
}
