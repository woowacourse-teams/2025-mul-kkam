package com.mulkkam.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_ACHIEVEMENT_RATE
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_CUP_ID
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_TARGET_AMOUNT
import com.mulkkam.domain.checker.IntakeChecker.Companion.KEY_INTAKE_CHECKER_TOTAL_AMOUNT
import com.mulkkam.domain.repository.CupsRepository
import com.mulkkam.domain.repository.MembersRepository
import java.time.LocalDate

class IntakeWidgetWorker(
    appContext: Context,
    params: WorkerParameters,
    private val membersRepository: MembersRepository,
    private val cupsRepository: CupsRepository,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result =
        runCatching {
            val progress = membersRepository.getMembersProgressInfo(LocalDate.now()).getOrError()
            val cups = cupsRepository.getCups().getOrError()
            val cupId = cups.representCup?.id

            workDataOf(
                KEY_INTAKE_CHECKER_ACHIEVEMENT_RATE to progress.achievementRate,
                KEY_INTAKE_CHECKER_TARGET_AMOUNT to progress.targetAmount,
                KEY_INTAKE_CHECKER_TOTAL_AMOUNT to progress.totalAmount,
                KEY_INTAKE_CHECKER_CUP_ID to cupId,
            )
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure() },
        )
}
