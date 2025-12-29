package com.mulkkam.di

import androidx.work.WorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val workManagerModule2 =
    module {
        single { WorkManager.getInstance(androidContext()) }
    }
