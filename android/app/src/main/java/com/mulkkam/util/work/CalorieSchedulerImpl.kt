package com.mulkkam.util.work

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mulkkam.domain.work.CalorieScheduler
import java.util.concurrent.TimeUnit

class CalorieSchedulerImpl(
    private val workManager: WorkManager,
) : CalorieScheduler {
    override fun scheduleCalorieCheck(intervalHours: Long) {
        val request = PeriodicWorkRequestBuilder<CalorieWorker>(intervalHours, TimeUnit.HOURS).build()
        workManager.enqueueUniquePeriodicWork(
            CHECK_CALORIE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    companion object {
        private const val CHECK_CALORIE_WORK_NAME = "CHECK_CALORIE_WORK"
    }
}
