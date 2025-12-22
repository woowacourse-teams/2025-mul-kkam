package com.mulkkam.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_CUP_ID
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_PERFORM_SUCCESS
import com.mulkkam.domain.repository.IntakeRepository
import kotlinx.datetime.toKotlinLocalDateTime

class DrinkByAmountWorker(
    appContext: Context,
    params: WorkerParameters,
    private val intakeRepository: IntakeRepository,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val cupId = inputData.getLong(KEY_INTAKE_CHECKER_CUP_ID, 0L)

        return runCatching {
            val now =
                java.time.LocalDateTime
                    .now()
                    .toKotlinLocalDateTime()
            intakeRepository
                .postIntakeHistoryCup(now, cupId)
                .getOrError()
        }.fold(
            onSuccess = { Result.success(workDataOf(KEY_INTAKE_CHECKER_PERFORM_SUCCESS to true)) },
            onFailure = { Result.failure() },
        )
    }
}
