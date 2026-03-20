package com.mulkkam.domain.checker

interface ProgressChecker {
    fun checkCurrentAchievementRate(): String

    companion object {
        const val KEY_PROGRESS_CHECKER_ACHIEVEMENT_RATE: String = "ACHIEVEMENT_RATE"
    }
}
