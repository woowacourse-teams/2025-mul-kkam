package com.mulkkam.di

import com.mulkkam.data.checker.AchievementHeatmapCheckerImpl
import com.mulkkam.data.checker.CalorieCheckerImpl
import com.mulkkam.data.checker.IntakeCheckerImpl
import com.mulkkam.data.checker.ProgressCheckerImpl
import com.mulkkam.domain.checker.AchievementHeatmapChecker
import com.mulkkam.domain.checker.CalorieChecker
import com.mulkkam.domain.checker.IntakeChecker
import com.mulkkam.domain.checker.ProgressChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CheckerModule {
    @Binds
    @Singleton
    abstract fun bindAchievementHeatmapChecker(impl: AchievementHeatmapCheckerImpl): AchievementHeatmapChecker

    @Binds
    @Singleton
    abstract fun bindCalorieChecker(impl: CalorieCheckerImpl): CalorieChecker

    @Binds
    @Singleton
    abstract fun bindProgressChecker(impl: ProgressCheckerImpl): ProgressChecker

    @Binds
    @Singleton
    abstract fun bindIntakeChecker(impl: IntakeCheckerImpl): IntakeChecker
}
