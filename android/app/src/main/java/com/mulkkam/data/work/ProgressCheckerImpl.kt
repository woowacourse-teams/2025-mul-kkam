package com.mulkkam.data.work

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mulkkam.domain.work.ProgressChecker
import java.util.UUID

class ProgressCheckerImpl(
    private val workManager: WorkManager,
) : ProgressChecker {
    override fun checkCurrentAchievementRate(): UUID {
        val workRequest = OneTimeWorkRequestBuilder<ProgressCheckWorker>().build()

        workManager.enqueue(workRequest)
        return workRequest.id
    }
}
