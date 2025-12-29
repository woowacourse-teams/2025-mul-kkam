package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.request.notification.ActiveCaloriesBurnedRequest
import com.mulkkam.data.remote.model.response.notifications.NotificationUnreadCountResponse
import com.mulkkam.data.remote.model.response.notifications.NotificationsResponse

// TODO: DataSource 구현 필요
class NotificationRemoteDataSourceImpl : NotificationRemoteDataSource {
    override suspend fun postActiveCaloriesBurned(activeCaloriesBurned: ActiveCaloriesBurnedRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getNotifications(
        lastId: Long?,
        clientTime: String,
        size: Int,
    ): Result<NotificationsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun postSuggestionNotificationsApproval(id: Long): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getNotificationsUnreadCount(): Result<NotificationUnreadCountResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNotifications(id: Long): Result<Unit> {
        TODO("Not yet implemented")
    }
}
