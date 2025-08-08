package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.NotificationsRequest
import com.mulkkam.data.remote.model.response.NotificationsResponse
import retrofit2.http.Body
import retrofit2.http.GET

interface NotificationsService {
    @GET("/notifications")
    suspend fun getNotifications(
        @Body notifications: NotificationsRequest,
    ): Result<NotificationsResponse>
}
