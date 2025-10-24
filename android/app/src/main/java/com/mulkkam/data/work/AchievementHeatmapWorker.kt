package com.mulkkam.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mulkkam.domain.checker.AchievementHeatmapChecker
import com.mulkkam.domain.checker.AchievementHeatmapChecker.Companion.TOTAL_CELL_COUNT
import com.mulkkam.domain.repository.IntakeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

@HiltWorker
class AchievementHeatmapWorker
    @AssistedInject
    constructor(
        @Assisted appContext: Context,
        @Assisted params: WorkerParameters,
        private val intakeRepository: IntakeRepository,
    ) : CoroutineWorker(appContext, params) {
        override suspend fun doWork(): Result =
            runCatching {
                val endDate: LocalDate = LocalDate.now()
                val startDate: LocalDate = endDate.minusDays((TOTAL_CELL_COUNT - 1).toLong())
                val achievementRates =
                    intakeRepository
                        .getAchievementRates(startDate, endDate)
                        .getOrError()

                workDataOf(AchievementHeatmapChecker.KEY_RATES to achievementRates.map { it.achievementRate }.toFloatArray())
            }.fold(
                onSuccess = { Result.success(it) },
                onFailure = { Result.failure() },
            )
    }
