package com.mulkkam.di

import android.content.Context
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.mulkkam.data.work.AchievementHeatmapWorker
import com.mulkkam.data.work.CalorieWorker
import com.mulkkam.data.work.DrinkByAmountWorker
import com.mulkkam.data.work.IntakeWorker
import com.mulkkam.data.work.ProgressWorker
import com.mulkkam.di.RepositoryInjection.cupsRepository
import com.mulkkam.di.RepositoryInjection.healthRepository
import com.mulkkam.di.RepositoryInjection.intakeRepository
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.di.RepositoryInjection.notificationRepository
import com.mulkkam.domain.repository.CupsRepository
import com.mulkkam.domain.repository.HealthRepository
import com.mulkkam.domain.repository.IntakeRepository
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.domain.repository.NotificationRepository

object WorkInjection {
    private var _workManager: WorkManager? = null
    val workManager: WorkManager
        get() = _workManager ?: throw IllegalStateException()

    fun init(context: Context) {
        if (_workManager == null) {
            val factory =
                MulKkamWorkerFactory(
                    healthRepository = healthRepository,
                    notificationRepository = notificationRepository,
                    membersRepository = membersRepository,
                    cupsRepository = cupsRepository,
                    intakeRepository = intakeRepository,
                )

            val config =
                Configuration
                    .Builder()
                    .setWorkerFactory(factory)
                    .build()

            WorkManager.initialize(context.applicationContext, config)
            _workManager = WorkManager.getInstance(context.applicationContext)
        }
    }
}

private class MulKkamWorkerFactory(
    private val healthRepository: HealthRepository,
    private val notificationRepository: NotificationRepository,
    private val membersRepository: MembersRepository,
    private val cupsRepository: CupsRepository,
    private val intakeRepository: IntakeRepository,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker =
        when (workerClassName) {
            CalorieWorker::class.java.name ->
                CalorieWorker(appContext, workerParameters, healthRepository, notificationRepository)

            AchievementHeatmapWorker::class.java.name ->
                AchievementHeatmapWorker(appContext, workerParameters, intakeRepository)

            ProgressWorker::class.java.name ->
                ProgressWorker(appContext, workerParameters, membersRepository)

            IntakeWorker::class.java.name ->
                IntakeWorker(appContext, workerParameters, membersRepository, cupsRepository)

            DrinkByAmountWorker::class.java.name ->
                DrinkByAmountWorker(appContext, workerParameters, intakeRepository)

            else -> throw IllegalArgumentException("Unknown worker class: $workerClassName")
        }
}
