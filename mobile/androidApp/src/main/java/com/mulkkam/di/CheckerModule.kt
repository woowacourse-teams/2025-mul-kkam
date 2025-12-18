package com.mulkkam.di

import com.mulkkam.data.checker.AchievementHeatmapCheckerImpl
import com.mulkkam.data.checker.CalorieCheckerImpl
import com.mulkkam.data.checker.IntakeCheckerImpl
import com.mulkkam.data.checker.ProgressCheckerImpl
import com.mulkkam.domain.checker.AchievementHeatmapChecker
import com.mulkkam.domain.checker.CalorieChecker
import com.mulkkam.domain.checker.IntakeChecker
import com.mulkkam.domain.checker.ProgressChecker
import org.koin.dsl.module

val checkerModule =
    module {
        single<AchievementHeatmapChecker> { AchievementHeatmapCheckerImpl(get()) }
        single<CalorieChecker> { CalorieCheckerImpl(get()) }
        single<ProgressChecker> { ProgressCheckerImpl(get()) }
        single<IntakeChecker> { IntakeCheckerImpl(get()) }
    }
