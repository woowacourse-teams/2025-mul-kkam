package com.mulkkam

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.mulkkam.di.PreferenceInjection

class MulKkamApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        PreferenceInjection.init(this)
    }
}
