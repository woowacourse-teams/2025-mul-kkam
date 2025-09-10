package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.notification.ActiveCaloriesBurnedRequest
import com.mulkkam.data.remote.model.response.notification.toDomain
import com.mulkkam.data.remote.service.NotificationsService
import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.NotificationRepository
import java.time.LocalDateTime

class NotificationRepositoryImpl(
    private val notificationService: NotificationsService,
) : NotificationRepository {
    override suspend fun getNotifications(
        time: LocalDateTime,
        size: Int,
    ): MulKkamResult<List<Notification>> {
        val result =
            notificationService.getNotifications(
                clientTime = time.toString(),
                size = size,
            )

        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun postActiveCaloriesBurned(kcal: Double): MulKkamResult<Unit> {
        val result =
            notificationService.postActiveCaloriesBurned(
                ActiveCaloriesBurnedRequest(kcal),
            )

        return result.fold(
            onSuccess = { MulKkamResult(data = Unit) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun getNotificationsUnreadCount(): MulKkamResult<Long> {
        val result = notificationService.getNotificationsUnreadCount()

        return result.fold(
            onSuccess = { MulKkamResult(data = it.count) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }
}
