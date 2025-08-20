package com.mulkkam.domain.work

import java.util.UUID

interface ProgressChecker {
    fun checkCurrentAchievementRate(): UUID
}
