package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.request.notification.ActiveCaloriesBurnedRequest
import com.mulkkam.data.remote.model.response.notifications.NotificationUnreadCountResponse
import com.mulkkam.data.remote.model.response.notifications.NotificationsResponse

interface NotificationDataSource {
    suspend fun postActiveCaloriesBurned(activeCaloriesBurned: ActiveCaloriesBurnedRequest): Result<Unit>

    suspend fun getNotifications(
        lastId: Long? = null,
        clientTime: String,
        size: Int,
    ): Result<NotificationsResponse>

    suspend fun postSuggestionNotificationsApproval(id: Long): Result<Unit>

    suspend fun getNotificationsUnreadCount(): Result<NotificationUnreadCountResponse>

    suspend fun deleteNotifications(id: Long): Result<Unit>
}
