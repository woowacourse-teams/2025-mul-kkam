package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.api.safeApiCall
import com.mulkkam.data.remote.api.safeApiCallUnit
import com.mulkkam.data.remote.model.request.notification.ActiveCaloriesBurnedRequest
import com.mulkkam.data.remote.model.response.notifications.NotificationUnreadCountResponse
import com.mulkkam.data.remote.model.response.notifications.NotificationsResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class NotificationRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : NotificationRemoteDataSource {
    override suspend fun postActiveCaloriesBurned(activeCaloriesBurned: ActiveCaloriesBurnedRequest): Result<Unit> =
        safeApiCallUnit {
            httpClient.post("/suggestion-notifications/activity") {
                setBody(activeCaloriesBurned)
            }
        }

    override suspend fun getNotifications(
        lastId: Long?,
        clientTime: String,
        size: Int,
    ): Result<NotificationsResponse> =
        safeApiCall {
            httpClient.get("/notifications") {
                url {
                    lastId?.let { parameters.append("lastId", it.toString()) }
                    parameters.append("clientTime", clientTime)
                    parameters.append("size", size.toString())
                }
            }
        }

    override suspend fun postSuggestionNotificationsApproval(id: Long): Result<Unit> =
        safeApiCallUnit {
            httpClient.post("/suggestion-notifications/approval/$id")
        }

    override suspend fun getNotificationsUnreadCount(): Result<NotificationUnreadCountResponse> =
        safeApiCall {
            httpClient.get("/notifications/unread-count")
        }

    override suspend fun deleteNotifications(id: Long): Result<Unit> =
        safeApiCallUnit {
            httpClient.delete("/notifications/$id")
        }
}
