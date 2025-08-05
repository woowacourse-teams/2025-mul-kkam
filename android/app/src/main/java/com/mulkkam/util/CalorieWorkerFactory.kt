package com.mulkkam.util

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.mulkkam.domain.repository.HealthRepository

class CalorieWorkerFactory(
    private val healthRepository: HealthRepository,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? =
        when (workerClassName) {
            CalorieWorker::class.java.name -> CalorieWorker(appContext, workerParameters, healthRepository)

            else -> null
        }
}
