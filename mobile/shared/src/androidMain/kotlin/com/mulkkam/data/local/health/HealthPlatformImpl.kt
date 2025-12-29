package com.mulkkam.data.local.health

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant

class HealthPlatformImpl(
    private val client: HealthConnectClient?,
) : HealthPlatform {
    override suspend fun getCalories(
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Double {
        val start = Instant.ofEpochMilli(startEpochMillis)
        val end = Instant.ofEpochMilli(endEpochMillis)
        val result =
            client?.aggregate(
                AggregateRequest(
                    metrics = setOf(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(start, end),
                ),
            )
        val kcal = result?.get(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL)?.inKilocalories ?: 0.0
        return kcal
    }

    override suspend fun hasPermissions(permissions: Set<String>): Boolean {
        val granted = client?.permissionController?.getGrantedPermissions() ?: emptySet()
        return granted.containsAll(permissions)
    }
}
