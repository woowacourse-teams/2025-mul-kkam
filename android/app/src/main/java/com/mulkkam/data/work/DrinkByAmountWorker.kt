package com.mulkkam.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_AMOUNT
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_PERFORM_SUCCESS
import com.mulkkam.domain.model.cups.CupAmount.Companion.MIN_ML
import com.mulkkam.domain.repository.IntakeRepository

class DrinkByAmountWorker(
    appContext: Context,
    params: WorkerParameters,
    private val intakeRepository: IntakeRepository,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val amount = inputData.getInt(KEY_INTAKE_CHECKER_AMOUNT, 0)
        if (amount < MIN_ML) return Result.failure()

        return runCatching {
            // TODO: 컵 이용 API 로 변경해야 됨
//            intakeRepository
//                .postIntakeHistoryInput(LocalDateTime.now(), CupAmount(amount))
//                .getOrError()
        }.fold(
            onSuccess = { Result.success(workDataOf(KEY_INTAKE_CHECKER_PERFORM_SUCCESS to true)) },
            onFailure = { Result.failure() },
        )
    }
}
