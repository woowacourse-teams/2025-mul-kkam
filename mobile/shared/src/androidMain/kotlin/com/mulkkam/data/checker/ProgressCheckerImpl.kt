package com.mulkkam.data.checker

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mulkkam.data.work.ProgressWorker
import com.mulkkam.domain.checker.ProgressChecker

class ProgressCheckerImpl(
    private val workManager: WorkManager,
) : ProgressChecker {
    override fun checkCurrentAchievementRate(): String {
        val request = OneTimeWorkRequestBuilder<ProgressWorker>().build()
        workManager.enqueue(request)
        return request.id.toString()
    }
}
