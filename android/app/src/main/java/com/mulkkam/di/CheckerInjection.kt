package com.mulkkam.di

import com.mulkkam.data.checker.AchievementHeatmapCheckerImpl
import com.mulkkam.data.checker.CalorieCheckerImpl
import com.mulkkam.data.checker.IntakeCheckerImpl
import com.mulkkam.data.checker.ProgressCheckerImpl
import com.mulkkam.di.WorkInjection.workManager
import com.mulkkam.domain.checker.AchievementHeatmapChecker
import com.mulkkam.domain.checker.CalorieChecker
import com.mulkkam.domain.checker.IntakeChecker
import com.mulkkam.domain.checker.ProgressChecker

object CheckerInjection {
    val achievementHeatmapChecker: AchievementHeatmapChecker by lazy {
        AchievementHeatmapCheckerImpl(workManager)
    }

    val calorieChecker: CalorieChecker by lazy {
        CalorieCheckerImpl(workManager)
    }

    val progressChecker: ProgressChecker by lazy {
        ProgressCheckerImpl(workManager)
    }

    val intakeChecker: IntakeChecker by lazy {
        IntakeCheckerImpl(workManager)
    }
}
