package com.mulkkam.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_CUP_ID
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_PERFORM_SUCCESS
import com.mulkkam.domain.repository.IntakeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDateTime

@HiltWorker
class DrinkByAmountWorker
    @AssistedInject
    constructor(
        @Assisted appContext: Context,
        @Assisted params: WorkerParameters,
        private val intakeRepository: IntakeRepository,
    ) : CoroutineWorker(appContext, params) {
        override suspend fun doWork(): Result {
            val cupId = inputData.getLong(KEY_INTAKE_CHECKER_CUP_ID, 0L)

            return runCatching {
                intakeRepository
                    .postIntakeHistoryCup(LocalDateTime.now(), cupId)
                    .getOrError()
            }.fold(
                onSuccess = { Result.success(workDataOf(KEY_INTAKE_CHECKER_PERFORM_SUCCESS to true)) },
                onFailure = { Result.failure() },
            )
        }
    }
