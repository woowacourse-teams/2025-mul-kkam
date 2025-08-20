package com.mulkkam.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mulkkam.domain.checker.IntakeChecker
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
            val cupAmount = cups.representCup?.amount?.value ?: 0

            workDataOf(
                IntakeChecker.KEY_OUTPUT_ACHIEVEMENT_RATE to progress.achievementRate,
                IntakeChecker.KEY_OUTPUT_TARGET to progress.targetAmount,
                IntakeChecker.KEY_OUTPUT_TOTAL to progress.totalAmount,
                IntakeChecker.KEY_OUTPUT_PRIMARY_CUP_AMOUNT to cupAmount,
            )
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure() },
        )
}
