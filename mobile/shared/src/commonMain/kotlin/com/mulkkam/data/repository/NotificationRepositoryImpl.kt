package com.mulkkam.data.repository

import com.mulkkam.data.remote.datasource.NotificationRemoteDataSource
import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.request.notification.ActiveCaloriesBurnedRequest
import com.mulkkam.data.remote.model.response.notifications.toDomain
import com.mulkkam.domain.model.notification.NotificationsResult
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.NotificationRepository
import kotlinx.datetime.LocalDateTime

class NotificationRepositoryImpl(
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
) : NotificationRepository {
    override suspend fun getNotifications(
        time: LocalDateTime,
        size: Int,
        lastId: Long?,
    ): MulKkamResult<NotificationsResult> {
        val result =
            notificationRemoteDataSource.getNotifications(
                lastId = lastId,
                clientTime = time.toString(),
                size = size,
            )

        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun postActiveCaloriesBurned(kcal: Double): MulKkamResult<Unit> {
        val result =
            notificationRemoteDataSource.postActiveCaloriesBurned(
                ActiveCaloriesBurnedRequest(kcal),
            )

        return result.fold(
            onSuccess = { MulKkamResult(data = Unit) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun postSuggestionNotificationsApproval(id: Long): MulKkamResult<Unit> {
        val result = notificationRemoteDataSource.postSuggestionNotificationsApproval(id)

        return result.fold(
            onSuccess = { MulKkamResult(data = Unit) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getNotificationsUnreadCount(): MulKkamResult<Long> {
        val result = notificationRemoteDataSource.getNotificationsUnreadCount()

        return result.fold(
            onSuccess = { MulKkamResult(data = it.count) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun deleteNotifications(id: Long): MulKkamResult<Unit> {
        val result = notificationRemoteDataSource.deleteNotifications(id)

        return result.fold(
            onSuccess = { MulKkamResult(data = Unit) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }
}
