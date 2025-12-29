package com.mulkkam

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.common.KakaoSdk
import com.mulkkam.data.logger.LoggerInitializer
import com.mulkkam.di.androidSharedModule
import com.mulkkam.domain.repository.DevicesRepository
import com.mulkkam.ui.service.NotificationService
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class MulKkamApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        KakaoSdk.init(this, BuildConfig.KEY_KAKAO)
        createNotificationChannel()

        startKoin {
            androidLogger()
            androidContext(this@MulKkamApp)
            workManagerFactory()
            modules(androidSharedModule(BuildConfig.BASE_URL, BuildConfig.DEBUG))
        }

        ProcessLifecycleOwner.get().lifecycleScope.launch {
            val devicesRepository: DevicesRepository = get()
            val loggerInitializer: LoggerInitializer = get()
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
}
