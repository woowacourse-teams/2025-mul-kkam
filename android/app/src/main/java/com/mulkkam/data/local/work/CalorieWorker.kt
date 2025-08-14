package com.mulkkam.data.local.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mulkkam.di.LoggingInjection.mulKkamLogger
import com.mulkkam.domain.model.logger.LogEvent
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
        val now = Instant.now()

        val burn =
            runCatching {
                healthRepository.getActiveCaloriesBurned(
                    now.minusSeconds(SECONDS_IN_TWO_HOURS),
                    now,
                )
            }.getOrElse {
                mulKkamLogger.error(
                    LogEvent.HEALTH_CONNECT,
                    "Calorie fetch failed: ${it::class.java.simpleName}: ${it.message}\n${it.stackTraceToString()}",
                )
                return Result.retry()
            }

        notificationRepository.postActiveCaloriesBurned(burn.kcal)
        return Result.success()
    }
}
