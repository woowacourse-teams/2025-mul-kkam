package com.mulkkam.di

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import com.mulkkam.data.work.WorkerFactory
import com.mulkkam.di.RepositoryInjection.cupsRepository
import com.mulkkam.di.RepositoryInjection.healthRepository
import com.mulkkam.di.RepositoryInjection.intakeRepository
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.di.RepositoryInjection.notificationRepository

object WorkInjection {
    private var _workManager: WorkManager? = null
    val workManager: WorkManager
        get() = _workManager ?: throw IllegalStateException()

    fun init(context: Context) {
        if (_workManager == null) {
            val config =
                Configuration
                    .Builder()
                    .setWorkerFactory(
                        WorkerFactory(healthRepository, notificationRepository, membersRepository, cupsRepository, intakeRepository),
                    ).build()

            WorkManager.initialize(context, config)
            _workManager = WorkManager.getInstance(context)
        }
    }
}
