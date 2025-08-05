package com.mulkkam.data.local.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mulkkam.domain.repository.HealthRepository
import com.mulkkam.domain.repository.HealthRepository.Companion.SECONDS_IN_TWO_HOURS
import java.time.Instant

class CalorieWorker(
    appContext: Context,
    params: WorkerParameters,
    private val healthRepository: HealthRepository,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val burn = healthRepository.getActiveCaloriesBurned(Instant.now().minusSeconds(SECONDS_IN_TWO_HOURS), Instant.now())
        Log.d("CalorieWorker", "[2시간 주기] 오늘 칼로리 소모량: ${burn.kcal} kcal")
        // TODO: 서버로 칼로리 소모량 전송
        return Result.success()
    }
}
