package com.mulkkam.domain.model.bio

interface HealthPlatform {
    fun isAvailable(): Boolean

    suspend fun navigateToHealthConnect()
}
