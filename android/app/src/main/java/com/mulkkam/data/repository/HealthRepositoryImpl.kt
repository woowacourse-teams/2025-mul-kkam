package com.mulkkam.data.repository

import com.mulkkam.data.remote.service.HealthService
import com.mulkkam.domain.model.CalorieBurn
import com.mulkkam.domain.repository.HealthRepository
import java.time.Instant

class HealthRepositoryImpl(
    private val service: HealthService,
) : HealthRepository {
    override suspend fun getActiveCaloriesBurned(
        start: Instant,
        end: Instant,
    ): CalorieBurn = CalorieBurn(service.getCalories(start, end))

    override suspend fun hasPermissions(permissions: Set<String>): Boolean = service.hasPermissions(permissions)
}
