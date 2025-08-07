package com.mulkkam.domain.model

data class MembersProgressInfo(
    val nickname: String,
    val streak: Int,
    val achievementRate: Double,
    val targetAmount: Int,
    val totalAmount: Int,
    val comment: String,
)
