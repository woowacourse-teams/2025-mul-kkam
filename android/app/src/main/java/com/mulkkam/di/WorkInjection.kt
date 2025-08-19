package com.mulkkam.di

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import com.mulkkam.data.local.work.CalorieSchedulerImpl
import com.mulkkam.data.local.work.ProgressCheckerImpl
import com.mulkkam.data.local.work.WorkerFactory
import com.mulkkam.di.RepositoryInjection.healthRepository
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.di.RepositoryInjection.notificationRepository
import com.mulkkam.domain.work.CalorieScheduler
import com.mulkkam.domain.work.ProgressChecker

object WorkInjection {
    private lateinit var workManager: WorkManager

    val calorieScheduler: CalorieScheduler by lazy {
        CalorieSchedulerImpl(workManager)
    }

    val progressChecker: ProgressChecker by lazy {
        ProgressCheckerImpl(workManager)
    }

    fun init(context: Context) {
        if (::workManager.isInitialized.not()) {
            val config =
                Configuration
                    .Builder()
                    .setWorkerFactory(WorkerFactory(healthRepository, notificationRepository, membersRepository))
                    .build()

            WorkManager.initialize(context, config)
            workManager = WorkManager.getInstance(context)
        }
    }
}
