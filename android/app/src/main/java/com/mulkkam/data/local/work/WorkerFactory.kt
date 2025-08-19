package com.mulkkam.data.local.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.mulkkam.domain.repository.HealthRepository
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.domain.repository.NotificationRepository

class WorkerFactory(
    private val healthRepository: HealthRepository,
    private val notificationRepository: NotificationRepository,
    private val membersRepository: MembersRepository,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? =
        when (workerClassName) {
            CalorieWorker::class.java.name -> CalorieWorker(appContext, workerParameters, healthRepository, notificationRepository)
            ProgressWidgetWorker::class.java.name -> ProgressWidgetWorker(appContext, workerParameters, membersRepository)
            IntakeWidgetWorker::class.java.name -> IntakeWidgetWorker(appContext, workerParameters, membersRepository)
            else -> null
        }
}
