package com.mulkkam.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.repository.HealthRepository
import com.mulkkam.domain.repository.HealthRepository.Companion.SECONDS_IN_TWO_HOURS
import com.mulkkam.domain.repository.NotificationRepository

class CalorieWorker(
    appContext: Context,
    params: WorkerParameters,
    private val healthRepository: HealthRepository,
    private val notificationRepository: NotificationRepository,
    private val logger: Logger,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val nowMillis = System.currentTimeMillis()
        val twoHoursAgoMillis = nowMillis - (SECONDS_IN_TWO_HOURS * 1000)

        logger.info(
            LogEvent.HEALTH_CONNECT,
            "CalorieWorker triggered at $nowMillis",
        )

        runCatching {
            healthRepository
                .getActiveCaloriesBurned(
                    twoHoursAgoMillis,
                    nowMillis,
                ).getOrError()
        }.onSuccess { exerciseCalorie ->
            if (exerciseCalorie.exercised.not()) return@onSuccess
            logger.info(
                LogEvent.HEALTH_CONNECT,
                "Posted active calories burned notification: ${exerciseCalorie.kcal} kcal",
            )
            notificationRepository.postActiveCaloriesBurned(exerciseCalorie.kcal)
        }.onFailure {
            logger.error(
                LogEvent.HEALTH_CONNECT,
                "Calorie fetch failed: ${it::class.java.simpleName}: ${it.message}\n${it.stackTraceToString()}",
            )
            return Result.retry()
        }

        return Result.success()
    }
}
