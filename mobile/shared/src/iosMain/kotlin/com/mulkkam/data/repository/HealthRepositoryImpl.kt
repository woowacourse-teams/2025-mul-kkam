package com.mulkkam.data.repository

import com.mulkkam.domain.model.bio.ExerciseCalorie
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.HealthRepository

// TODO: iOS HealthKit 연동 필요
class HealthRepositoryImpl : HealthRepository {
    override suspend fun getActiveCaloriesBurned(
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): MulKkamResult<ExerciseCalorie> = MulKkamResult(data = ExerciseCalorie(kcal = 0.0))

    override suspend fun hasPermissions(permissions: Set<String>): Boolean = false
}
