package com.mulkkam.domain.model.bio

interface HealthManager {
    fun isAvailable(): Boolean

    fun navigateToHealthConnect()
}
