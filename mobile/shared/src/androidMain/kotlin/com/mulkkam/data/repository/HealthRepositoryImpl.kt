package com.mulkkam.data.repository

import com.mulkkam.data.local.health.HealthConnect
import com.mulkkam.domain.model.bio.ExerciseCalorie
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.HealthRepository
import java.time.Instant

class HealthRepositoryImpl(
    private val healthConnect: HealthConnect,
) : HealthRepository {
    override suspend fun getActiveCaloriesBurned(
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): MulKkamResult<ExerciseCalorie> =
        runCatching {
            val start = Instant.ofEpochMilli(startEpochMillis)
            val end = Instant.ofEpochMilli(endEpochMillis)
            val kcal = healthConnect.getCalories(start, end)
            ExerciseCalorie(kcal)
        }.fold(
            onSuccess = { MulKkamResult(data = it) },
            onFailure = { MulKkamResult(error = it.toMulKkamError()) },
        )

    override suspend fun hasPermissions(permissions: Set<String>): Boolean = healthConnect.hasPermissions(permissions)
}
