package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.api.safeApiCall
import com.mulkkam.data.remote.api.safeApiCallUnit
import com.mulkkam.data.remote.model.request.reminder.ReminderRequest
import com.mulkkam.data.remote.model.response.reminder.ReminderResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class ReminderRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : ReminderRemoteDataSource {
    override suspend fun getReminder(): Result<ReminderResponse> =
        safeApiCall {
            httpClient.get("/reminder")
        }

    override suspend fun postReminder(reminder: ReminderRequest): Result<Unit> =
        safeApiCallUnit {
            httpClient.post("/reminder") {
                setBody(reminder)
            }
        }

    override suspend fun patchReminder(reminder: ReminderRequest): Result<Unit> =
        safeApiCallUnit {
            httpClient.patch("/reminder") {
                setBody(reminder)
            }
        }

    override suspend fun deleteReminder(id: Long): Result<Unit> =
        safeApiCallUnit {
            httpClient.delete("/reminder/$id")
        }
}
