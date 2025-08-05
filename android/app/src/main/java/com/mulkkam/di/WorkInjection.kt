package com.mulkkam.di

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import com.mulkkam.di.RepositoryInjection.healthRepository
import com.mulkkam.util.CalorieWorkerFactory

object WorkInjection {
    private var _workManager: WorkManager? = null
    val workManager: WorkManager
        get() = _workManager ?: throw IllegalArgumentException()

    fun init(context: Context) {
        if (_workManager == null) {
            val config =
                Configuration
                    .Builder()
                    .setWorkerFactory(CalorieWorkerFactory(healthRepository))
                    .build()

            WorkManager.initialize(context, config)
            _workManager = WorkManager.getInstance(context)
        }
    }
}
