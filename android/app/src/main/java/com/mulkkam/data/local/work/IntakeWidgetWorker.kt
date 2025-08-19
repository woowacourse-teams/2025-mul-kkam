package com.mulkkam.data.local.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mulkkam.domain.repository.MembersRepository
import java.time.LocalDate

class IntakeWidgetWorker(
    appContext: Context,
    params: WorkerParameters,
    private val membersRepository: MembersRepository,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result =
        runCatching {
            membersRepository.getMembersProgressInfo(LocalDate.now()).getOrError()
        }.fold(
            onSuccess = { info ->
                Result.success(
                    workDataOf(
                        KEY_OUTPUT_ACHIEVEMENT_RATE to info.achievementRate,
                        KEY_OUTPUT_TARGET to info.targetAmount,
                        KEY_OUTPUT_TOTAL to info.totalAmount,
                    ),
                )
            },
            onFailure = { Result.failure() },
        )

    companion object {
        const val KEY_OUTPUT_ACHIEVEMENT_RATE = "ACHIEVEMENT_RATE"
        const val KEY_OUTPUT_TARGET = "TARGET_AMOUNT"
        const val KEY_OUTPUT_TOTAL = "TOTAL_AMOUNT"
    }
}
