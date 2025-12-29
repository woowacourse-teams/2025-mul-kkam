package com.mulkkam.data.local.health

interface HealthPlatform {
    suspend fun getCalories(
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Double

    suspend fun hasPermissions(permissions: Set<String>): Boolean
}
