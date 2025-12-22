package com.mulkkam.domain.repository

import com.mulkkam.domain.model.bio.ExerciseCalorie
import com.mulkkam.domain.model.result.MulKkamResult

interface HealthRepository {
    suspend fun getActiveCaloriesBurned(
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): MulKkamResult<ExerciseCalorie>

    suspend fun hasPermissions(permissions: Set<String>): Boolean

    companion object {
        const val SECONDS_IN_TWO_HOURS: Long = 3600 * 2L
    }
}
