package com.mulkkam.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mulkkam.domain.checker.ProgressChecker.Companion.KEY_PROGRESS_CHECKER_ACHIEVEMENT_RATE
import com.mulkkam.domain.repository.MembersRepository
import kotlinx.datetime.toKotlinLocalDate

class ProgressWorker(
    appContext: Context,
    params: WorkerParameters,
    private val membersRepository: MembersRepository,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result =
        runCatching {
            val today =
                java.time.LocalDate
                    .now()
                    .toKotlinLocalDate()
            membersRepository
                .getMembersProgressInfo(today)
                .getOrError()
        }.fold(
            onSuccess = { membersProgressInfo ->
                val outputData =
                    workDataOf(
                        KEY_PROGRESS_CHECKER_ACHIEVEMENT_RATE to membersProgressInfo.achievementRate,
                    )
                Result.success(outputData)
            },
            onFailure = {
                Result.failure()
            },
        )
}
