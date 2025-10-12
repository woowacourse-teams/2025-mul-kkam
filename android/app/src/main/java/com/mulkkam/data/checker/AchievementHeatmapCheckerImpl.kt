package com.mulkkam.data.checker

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mulkkam.data.work.AchievementHeatmapWorker
import com.mulkkam.domain.checker.AchievementHeatmapChecker
import java.util.UUID

class AchievementHeatmapCheckerImpl(
    private val workManager: WorkManager,
) : AchievementHeatmapChecker {
    override fun fetchAchievementHeatmap(): UUID {
        val request = OneTimeWorkRequestBuilder<AchievementHeatmapWorker>().build()
        workManager.enqueue(request)
        return request.id
    }
}
