package com.mulkkam.data.local.service

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant

class HealthService(
    private val client: HealthConnectClient?,
) {
    suspend fun getCalories(
        start: Instant,
        end: Instant,
    ): Double {
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

    suspend fun hasPermissions(permissions: Set<String>): Boolean {
        val granted = client?.permissionController?.getGrantedPermissions() ?: emptySet()
        return granted.containsAll(permissions)
    }
}
