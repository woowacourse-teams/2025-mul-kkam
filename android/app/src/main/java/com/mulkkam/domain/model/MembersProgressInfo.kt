package com.mulkkam.domain.model

data class MembersProgressInfo(
    val nickname: String,
    val streak: Int,
    val achievementRate: Float,
    val targetAmount: Int,
    val totalAmount: Int,
    val comment: String,
) {
    fun updateIntakeResult(
        amount: Int,
        achievementRate: Float,
    ): MembersProgressInfo =
        copy(
            totalAmount = totalAmount + amount,
            achievementRate = achievementRate,
        )
}
