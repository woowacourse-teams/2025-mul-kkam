package com.mulkkam.di

import com.mulkkam.domain.logger.FirebaseLoggingBridge
import com.mulkkam.domain.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

class KoinHelper {
    private val notificationRepository: NotificationRepository by lazy {
        KoinPlatform.getKoin().get()
    }

    fun initKoin(
        baseUrl: String,
        isDebug: Boolean,
        firebaseBridge: FirebaseLoggingBridge,
    ) {
        startKoin {
            modules(iosSharedModule(baseUrl, isDebug, firebaseBridge))
        }
    }

    fun postActiveCaloriesBurned(kcal: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            notificationRepository.postActiveCaloriesBurned(kcal)
        }
    }
}
