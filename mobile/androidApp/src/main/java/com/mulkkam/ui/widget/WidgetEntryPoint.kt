package com.mulkkam.ui.widget

import android.content.Context
import com.mulkkam.domain.checker.AchievementHeatmapChecker
import com.mulkkam.domain.checker.IntakeChecker
import com.mulkkam.domain.checker.ProgressChecker
import com.mulkkam.domain.logger.Logger
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun achievementHeatmapChecker(): AchievementHeatmapChecker

    fun intakeChecker(): IntakeChecker

    fun progressChecker(): ProgressChecker

    fun logger(): Logger
}

internal fun Context.widgetEntryPoint(): WidgetEntryPoint =
    EntryPointAccessors.fromApplication(
        applicationContext,
        WidgetEntryPoint::class.java,
    )
