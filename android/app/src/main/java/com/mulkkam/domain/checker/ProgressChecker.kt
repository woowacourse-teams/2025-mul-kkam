package com.mulkkam.domain.checker

import java.util.UUID

interface ProgressChecker {
    fun checkCurrentAchievementRate(): UUID

    companion object {
        const val KEY_OUTPUT_ACHIEVEMENT_RATE = "ACHIEVEMENT_RATE"
    }
}
