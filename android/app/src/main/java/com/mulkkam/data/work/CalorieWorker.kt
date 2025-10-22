package com.mulkkam.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.repository.HealthRepository
import com.mulkkam.domain.repository.HealthRepository.Companion.SECONDS_IN_TWO_HOURS
import com.mulkkam.domain.repository.NotificationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant

@HiltWorker
class CalorieWorker
    @AssistedInject
    constructor(
        @Assisted appContext: Context,
        @Assisted params: WorkerParameters,
        private val healthRepository: HealthRepository,
        private val notificationRepository: NotificationRepository,
        private val logger: Logger,
    ) : CoroutineWorker(appContext, params) {
        override suspend fun doWork(): Result {
            val now = Instant.now()

            logger.info(
                LogEvent.HEALTH_CONNECT,
                "CalorieWorker triggered at $now",
            )

            runCatching {
                healthRepository
                    .getActiveCaloriesBurned(
                        now.minusSeconds(SECONDS_IN_TWO_HOURS),
                        now,
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
