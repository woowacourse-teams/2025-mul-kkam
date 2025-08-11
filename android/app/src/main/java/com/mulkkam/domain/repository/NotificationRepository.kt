package com.mulkkam.domain.repository

import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.domain.model.result.MulKkamResult
import java.time.LocalDateTime

interface NotificationRepository {
    suspend fun getNotifications(
        time: LocalDateTime,
        size: Int,
    ): MulKkamResult<List<Notification>>

    suspend fun postActiveCaloriesBurned(kcal: Double): MulKkamResult<Unit>
}
