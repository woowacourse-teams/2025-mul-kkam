package com.mulkkam.di

import androidx.work.WorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val workManagerModule２ =
    module {
        single { WorkManager.getInstance(androidContext()) }
    }
