package com.mulkkam.domain.model

import com.mulkkam.domain.Alarm
import java.time.LocalDateTime

data class Notification(
    val id: Int,
    val title: String,
    val type: Alarm,
    val createdAt: LocalDateTime,
    val recommendedTargetAmount: Int,
    val isRead: Boolean,
)
