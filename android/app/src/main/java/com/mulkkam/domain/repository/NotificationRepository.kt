package com.mulkkam.domain.repository

import com.mulkkam.domain.MulKkamResult

interface NotificationRepository {
    suspend fun postActiveCaloriesBurned(kcal: Double): MulKkamResult<Unit>
}
