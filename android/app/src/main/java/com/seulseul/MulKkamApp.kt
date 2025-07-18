package com.seulseul

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MulKkamApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
