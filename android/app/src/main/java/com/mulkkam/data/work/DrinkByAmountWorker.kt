package com.mulkkam.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.cups.CupAmount.Companion.MIN_ML
import com.mulkkam.domain.repository.IntakeRepository
import java.time.LocalDateTime

class DrinkByAmountWorker(
    appContext: Context,
    params: WorkerParameters,
    private val intakeRepository: IntakeRepository,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val amount = inputData.getInt(KEY_INPUT_AMOUNT, 0)
        if (amount < MIN_ML) return Result.failure()

        return runCatching {
            intakeRepository
                .postIntakeHistory(LocalDateTime.now(), CupAmount(amount))
                .getOrError()
        }.fold(
            onSuccess = { Result.success(workDataOf(KEY_PERFORM_SUCCESS to true)) },
            onFailure = { Result.failure() },
        )
    }

    companion object {
        const val KEY_INPUT_AMOUNT = "INPUT_AMOUNT"
        const val KEY_PERFORM_SUCCESS = "OUTPUT_SUCCESS"
    }
}
