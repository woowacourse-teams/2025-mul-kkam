package com.mulkkam.data.checker

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mulkkam.data.work.ProgressWorker
import com.mulkkam.domain.checker.ProgressChecker
import java.util.UUID

class ProgressCheckerImpl(
    private val workManager: WorkManager,
) : ProgressChecker {
    override fun checkCurrentAchievementRate(): UUID {
        val workRequest = OneTimeWorkRequestBuilder<ProgressWorker>().build()

        workManager.enqueue(workRequest)
        return workRequest.id
    }
}
