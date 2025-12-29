package com.mulkkam.domain.checker

import java.util.UUID

interface ProgressChecker {
    fun checkCurrentAchievementRate(): UUID

    companion object {
        const val KEY_PROGRESS_CHECKER_ACHIEVEMENT_RATE: String = "ACHIEVEMENT_RATE"
    }
}
