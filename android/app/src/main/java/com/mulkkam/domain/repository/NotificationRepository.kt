package com.mulkkam.domain.repository

import com.mulkkam.domain.model.MulKkamResult
import com.mulkkam.domain.model.Notification
import java.time.LocalDateTime

interface NotificationRepository {
    suspend fun getNotifications(
        time: LocalDateTime,
        size: Int,
    ): MulKkamResult<List<Notification>>

    suspend fun postActiveCaloriesBurned(kcal: Double): MulKkamResult<Unit>
}
