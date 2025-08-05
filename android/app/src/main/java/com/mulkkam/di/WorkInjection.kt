package com.mulkkam.di

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import com.mulkkam.di.RepositoryInjection.healthRepository
import com.mulkkam.domain.work.CalorieScheduler
import com.mulkkam.util.work.CalorieSchedulerImpl
import com.mulkkam.util.work.WorkerFactory

object WorkInjection {
    private lateinit var workManager: WorkManager

    val calorieScheduler: CalorieScheduler by lazy {
        CalorieSchedulerImpl(workManager)
    }

    fun init(context: Context) {
        if (::workManager.isInitialized.not()) {
            val config =
                Configuration
                    .Builder()
                    .setWorkerFactory(WorkerFactory(healthRepository))
                    .build()

            WorkManager.initialize(context, config)
            workManager = WorkManager.getInstance(context)
        }
    }
}
