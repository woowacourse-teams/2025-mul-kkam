package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.NotificationsActivityRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationService {
    @POST("/notifications/activity")
    suspend fun postNotificationsActivity(
        @Body notificationsActivityRequest: NotificationsActivityRequest,
    ): Result<Unit>
}
