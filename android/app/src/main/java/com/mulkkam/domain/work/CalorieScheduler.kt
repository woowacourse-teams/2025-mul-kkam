package com.mulkkam.domain.work

interface CalorieScheduler {
    fun scheduleCalorieCheck(intervalHours: Long)

    companion object {
        const val DEFAULT_CHECK_CALORIE_INTERVAL_HOURS: Long = 2L
    }
}
