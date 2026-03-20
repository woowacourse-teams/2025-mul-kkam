package com.mulkkam.di

import com.mulkkam.data.work.AchievementHeatmapWorker
import com.mulkkam.data.work.CalorieWorker
import com.mulkkam.data.work.DrinkByAmountWorker
import com.mulkkam.data.work.IntakeWorker
import com.mulkkam.data.work.ProgressWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workerModule =
    module {
        worker { AchievementHeatmapWorker(get(), get(), get()) }
        worker { IntakeWorker(get(), get(), get(), get()) }
        worker { CalorieWorker(get(), get(), get(), get(), get()) }
        worker { DrinkByAmountWorker(get(), get(), get()) }
        worker { ProgressWorker(get(), get(), get()) }
    }
