package com.mulkkam.data.checker

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mulkkam.data.work.AchievementHeatmapWorker
import com.mulkkam.domain.checker.AchievementHeatmapChecker

class AchievementHeatmapCheckerImpl(
    private val workManager: WorkManager,
) : AchievementHeatmapChecker {
    override fun fetchAchievementHeatmap(): String {
        val request = OneTimeWorkRequestBuilder<AchievementHeatmapWorker>().build()
        workManager.enqueue(request)
        return request.id.toString()
    }
}
