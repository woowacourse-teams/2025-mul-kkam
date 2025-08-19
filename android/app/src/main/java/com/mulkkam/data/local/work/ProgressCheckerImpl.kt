package com.mulkkam.data.local.work

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mulkkam.domain.work.ProgressChecker

class ProgressCheckerImpl(
    private val workManager: WorkManager,
) : ProgressChecker {
    override fun checkCurrentAchievementRate() {
        val workRequest = OneTimeWorkRequestBuilder<ProgressCheckWorker>().build()

        workManager.enqueue(workRequest)
    }
}
