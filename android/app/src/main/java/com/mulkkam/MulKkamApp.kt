package com.mulkkam

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.appcompat.app.AppCompatDelegate
import com.kakao.sdk.common.KakaoSdk
import com.mulkkam.di.HealthConnectInjection
import com.mulkkam.di.LoggingInjection
import com.mulkkam.di.PreferenceInjection
import com.mulkkam.di.WorkInjection
import com.mulkkam.ui.service.NotificationService
import timber.log.Timber

class MulKkamApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        HealthConnectInjection.init(this)
        PreferenceInjection.init(this)
        WorkInjection.init(this)
        KakaoSdk.init(this, BuildConfig.KEY_KAKAO)
        createNotificationChannels()
        initLogger()
    }

    private fun createNotificationChannels() {
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

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(LoggingInjection.debugTimberTree)
        } else {
            Timber.plant(LoggingInjection.releaseTimberTree)
        }
    }
}
