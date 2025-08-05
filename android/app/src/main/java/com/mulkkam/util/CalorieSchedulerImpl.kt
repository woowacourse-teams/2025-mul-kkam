package com.mulkkam.util

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mulkkam.di.WorkInjection.workManager
import java.util.concurrent.TimeUnit

class CalorieSchedulerImpl(
    private val workManager: WorkManager,
) {
    fun scheduleCalorieCheck(intervalHours: Long) {
        val request = PeriodicWorkRequestBuilder<CalorieWorker>(intervalHours, TimeUnit.HOURS).build()
        workManager.enqueueUniquePeriodicWork(
            CHECK_CALORIE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    companion object {
        private const val CHECK_CALORIE_WORK_NAME = "CHECK_CALORIE_WORK"

        @Volatile
        private var instance: CalorieSchedulerImpl? = null

        fun getOrCreate(): CalorieSchedulerImpl =
            instance ?: synchronized(this) {
                instance ?: CalorieSchedulerImpl(workManager).also {
                    instance = it
                }
            }
    }
}
