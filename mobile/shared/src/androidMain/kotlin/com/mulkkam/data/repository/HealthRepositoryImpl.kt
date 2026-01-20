package com.mulkkam.data.repository

import com.mulkkam.data.local.health.HealthApi
import com.mulkkam.domain.model.bio.ExerciseCalorie
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.HealthRepository

class HealthRepositoryImpl(
    private val healthPlatform: HealthApi,
) : HealthRepository {
    override suspend fun getActiveCaloriesBurned(
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): MulKkamResult<ExerciseCalorie> =
        runCatching {
            val kcal = healthPlatform.getCalories(startEpochMillis, endEpochMillis)
            ExerciseCalorie(kcal)
        }.fold(
            onSuccess = { MulKkamResult(data = it) },
            onFailure = { MulKkamResult(error = it.toMulKkamError()) },
        )

    override suspend fun hasPermissions(permissions: Set<String>): Boolean = healthPlatform.hasPermissions(permissions)
}
