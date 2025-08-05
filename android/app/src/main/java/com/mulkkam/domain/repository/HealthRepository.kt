package com.mulkkam.domain.repository

import com.mulkkam.domain.model.CalorieBurn
import java.time.Instant

interface HealthRepository {
    suspend fun getActiveCaloriesBurned(
        start: Instant,
        end: Instant,
    ): CalorieBurn

    suspend fun hasPermissions(permissions: Set<String>): Boolean

    companion object {
        const val DEFAULT_CHECK_CALORIE_INTERVAL_HOURS: Long = 2L
        const val SECONDS_IN_TWO_HOURS: Long = 3600 * 2L
    }
}
