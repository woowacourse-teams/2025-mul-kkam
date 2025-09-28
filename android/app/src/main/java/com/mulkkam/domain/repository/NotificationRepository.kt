package com.mulkkam.domain.repository

import com.mulkkam.domain.model.notification.NotificationsResult
import com.mulkkam.domain.model.result.MulKkamResult
import java.time.LocalDateTime

interface NotificationRepository {
    suspend fun getNotifications(
        time: LocalDateTime,
        size: Int,
        lastId: Long? = null,
    ): MulKkamResult<NotificationsResult>

    suspend fun postActiveCaloriesBurned(kcal: Double): MulKkamResult<Unit>

    suspend fun getNotificationsUnreadCount(): MulKkamResult<Long>

    suspend fun postSuggestionNotificationsApproval(id: Long): MulKkamResult<Unit>

    suspend fun deleteNotifications(id: Long): MulKkamResult<Unit>
}
