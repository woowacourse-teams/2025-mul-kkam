package com.mulkkam.data.checker

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mulkkam.data.work.CalorieWorker
import com.mulkkam.domain.checker.CalorieChecker
import java.util.concurrent.TimeUnit

class CalorieCheckerImpl(
    private val workManager: WorkManager,
) : CalorieChecker {
    override fun checkCalorie(intervalHours: Long) {
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
