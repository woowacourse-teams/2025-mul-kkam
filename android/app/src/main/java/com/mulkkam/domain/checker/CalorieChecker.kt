package com.mulkkam.domain.checker

interface CalorieChecker {
    fun checkCalorie(intervalHours: Long)

    companion object {
        const val DEFAULT_CHECK_CALORIE_INTERVAL_HOURS: Long = 2L
    }
}
