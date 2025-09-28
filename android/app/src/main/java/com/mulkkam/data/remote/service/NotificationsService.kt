package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.notification.ActiveCaloriesBurnedRequest
import com.mulkkam.data.remote.model.response.notifications.NotificationUnreadCountResponse
import com.mulkkam.data.remote.model.response.notifications.NotificationsResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationsService {
    @POST("/suggestion-notifications/activity")
    suspend fun postActiveCaloriesBurned(
        @Body activeCaloriesBurned: ActiveCaloriesBurnedRequest,
    ): Result<Unit>

    @GET("/notifications")
    suspend fun getNotifications(
        @Query("lastId") lastId: Long? = null,
        @Query("clientTime") clientTime: String,
        @Query("size") size: Int,
    ): Result<NotificationsResponse>

    @POST("/suggestion-notifications/approval/{id}")
    suspend fun postSuggestionNotificationsApproval(
        @Path("id") id: Long,
    ): Result<Unit>

    @GET("/notifications/unread-count")
    suspend fun getNotificationsUnreadCount(): Result<NotificationUnreadCountResponse>

    @DELETE("/notifications/{id}")
    suspend fun deleteNotifications(
        @Path("id") id: Long,
    ): Result<Unit>
}
