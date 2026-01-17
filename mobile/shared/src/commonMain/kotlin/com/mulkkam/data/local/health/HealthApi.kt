package com.mulkkam.data.local.health

interface HealthApi {
    suspend fun getCalories(
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Double

    suspend fun hasPermissions(permissions: Set<String>): Boolean
}
