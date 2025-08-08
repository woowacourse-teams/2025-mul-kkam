package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.ActiveCaloriesBurnedRequest
import com.mulkkam.data.remote.model.response.NotificationsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NotificationsService {
    @POST("/notifications/activity")
    suspend fun postActiveCaloriesBurned(
        @Body activeCaloriesBurned: ActiveCaloriesBurnedRequest,
    ): Result<Unit>

    @GET("/notifications")
    suspend fun getNotifications(
        @Query("lastId") lastId: Int? = null,
        @Query("clientTime") clientTime: String,
        @Query("size") size: Int,
    ): Result<NotificationsResponse>
}
