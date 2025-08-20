package com.mulkkam.data.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.mulkkam.domain.repository.CupsRepository
import com.mulkkam.domain.repository.HealthRepository
import com.mulkkam.domain.repository.IntakeRepository
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.domain.repository.NotificationRepository

class WorkerFactory(
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
            CalorieWorker::class.java.name -> CalorieWorker(appContext, workerParameters, healthRepository, notificationRepository)
            ProgressCheckWorker::class.java.name -> ProgressCheckWorker(appContext, workerParameters, membersRepository)
            IntakeWidgetWorker::class.java.name -> IntakeWidgetWorker(appContext, workerParameters, membersRepository, cupsRepository)
            DrinkByAmountWorker::class.java.name -> DrinkByAmountWorker(appContext, workerParameters, intakeRepository)
            else -> throw IllegalArgumentException("Unknown worker class: $workerClassName")
        }
}
