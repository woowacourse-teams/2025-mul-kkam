package com.mulkkam.data.checker

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mulkkam.data.work.ProgressWorker
import com.mulkkam.domain.checker.ProgressChecker
import java.util.UUID
import javax.inject.Inject

class ProgressCheckerImpl
    @Inject
    constructor(
        private val workManager: WorkManager,
    ) : ProgressChecker {
        override fun checkCurrentAchievementRate(): UUID {
            val request = OneTimeWorkRequestBuilder<ProgressWorker>().build()
            workManager.enqueue(request)
            return request.id
        }
    }
