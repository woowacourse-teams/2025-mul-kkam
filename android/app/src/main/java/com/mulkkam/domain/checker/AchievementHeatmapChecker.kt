package com.mulkkam.domain.checker

import java.util.UUID

interface AchievementHeatmapChecker {
    fun fetchAchievementHeatmap(): UUID

    companion object {
        const val COLUMN_COUNT: Int = 7
        const val TOTAL_CELL_COUNT: Int = COLUMN_COUNT * COLUMN_COUNT

        const val KEY_RATES: String = "KEY_ACHIEVEMENT_HEATMAP_RATES"
    }
}
