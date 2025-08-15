package com.mulkkam.data.repository

import com.mulkkam.data.local.service.HealthService
import com.mulkkam.domain.model.bio.CalorieBurn
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.HealthRepository
import java.time.Instant

class HealthRepositoryImpl(
    private val service: HealthService,
) : HealthRepository {
    override suspend fun getActiveCaloriesBurned(
        start: Instant,
        end: Instant,
    ): MulKkamResult<CalorieBurn> =
        runCatching {
            val kcal = service.getCalories(start, end)
            CalorieBurn(kcal)
        }.fold(
            onSuccess = { MulKkamResult(data = it) },
            onFailure = { MulKkamResult(error = it.toMulKkamError()) },
        )

    override suspend fun hasPermissions(permissions: Set<String>): Boolean = service.hasPermissions(permissions)
}
