package com.mulkkam.domain.model

data class TodayProgressInfo(
    val nickname: String,
    val streak: Int,
    val achievementRate: Float,
    val targetAmount: Int,
    val totalAmount: Int,
    val comment: String,
) {
    fun updateProgressInfo(
        amount: Int,
        achievementRate: Float,
    ): TodayProgressInfo =
        copy(
            totalAmount = totalAmount + amount,
            achievementRate = achievementRate,
        )
}
