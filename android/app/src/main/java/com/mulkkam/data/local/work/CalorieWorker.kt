package com.mulkkam.data.local.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mulkkam.domain.repository.HealthRepository
import com.mulkkam.domain.repository.HealthRepository.Companion.SECONDS_IN_TWO_HOURS
import com.mulkkam.domain.repository.NotificationRepository
import java.time.Instant

class CalorieWorker(
    appContext: Context,
    params: WorkerParameters,
    private val healthRepository: HealthRepository,
    private val notificationRepository: NotificationRepository,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val burn = healthRepository.getActiveCaloriesBurned(Instant.now().minusSeconds(SECONDS_IN_TWO_HOURS), Instant.now())
        notificationRepository.postActiveCaloriesBurned(burn.kcal)
        return Result.success()
    }
}
