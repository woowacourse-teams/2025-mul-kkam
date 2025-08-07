package com.mulkkam.domain.model

data class Cup(
    val id: Long,
    val nickname: String,
    val amount: Int,
    val rank: Int,
    val intakeType: IntakeType,
    val emoji: String,
)
