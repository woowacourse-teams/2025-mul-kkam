package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.reminder.ReminderRequest
import com.mulkkam.data.remote.model.response.reminder.ReminderResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ReminderService {
    @GET("/reminder")
    suspend fun getReminder(): Result<ReminderResponse>

    @POST("/reminder")
    suspend fun postReminder(
        @Body reminder: ReminderRequest,
    ): Result<Unit>

    @PATCH("/reminder")
    suspend fun patchReminder(
        @Body reminder: ReminderRequest,
    ): Result<Unit>

    @DELETE("/reminder/{id}")
    suspend fun deleteReminder(
        @Path("id") id: Long,
    ): Result<Unit>
}
