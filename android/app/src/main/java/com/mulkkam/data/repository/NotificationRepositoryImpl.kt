package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.ActiveCaloriesBurnedRequest
import com.mulkkam.data.remote.service.NotificationService
import com.mulkkam.domain.MulKkamResult
import com.mulkkam.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val notificationService: NotificationService,
) : NotificationRepository {
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
}
