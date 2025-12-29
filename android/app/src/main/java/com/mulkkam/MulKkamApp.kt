package com.mulkkam

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.Configuration
import com.kakao.sdk.common.KakaoSdk
import com.mulkkam.data.logger.LoggerInitializer
import com.mulkkam.domain.repository.DevicesRepository
import com.mulkkam.ui.service.NotificationService
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MulKkamApp :
    Application(),
    Configuration.Provider {
    @Inject
    lateinit var loggerInitializer: LoggerInitializer

    @Inject
    lateinit var devicesRepository: DevicesRepository

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        KakaoSdk.init(this, BuildConfig.KEY_KAKAO)
        createNotificationChannel()

        ProcessLifecycleOwner.get().lifecycleScope.launch {
            runCatching {
                devicesRepository.getDeviceUuid().getOrError()
            }.onSuccess { userId ->
                loggerInitializer.initialize(userId, BuildConfig.DEBUG)
            }
        }
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                NotificationService.CHANNEL_ID,
                NotificationService.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = NotificationService.CHANNEL_DESC
            }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
}
