package com.mulkkam.domain.model.members

import kotlinx.serialization.Serializable

@Serializable
data class TodayProgressInfo(
    val nickname: String,
    val streak: Int,
    val achievementRate: Float,
    val targetAmount: Int,
    val totalAmount: Int,
    val comment: String,
) {
    fun updateProgressInfo(
        amountDelta: Int,
        achievementRate: Float,
        comment: String,
    ): TodayProgressInfo =
        copy(
            totalAmount = totalAmount + amountDelta,
            achievementRate = achievementRate,
            comment = comment,
        )

    companion object {
        val EMPTY_TODAY_PROGRESS_INFO: TodayProgressInfo =
            TodayProgressInfo(
                nickname = "",
                streak = 0,
                achievementRate = 0.0f,
                targetAmount = 0,
                totalAmount = 0,
                comment = "",
            )
    }
}
