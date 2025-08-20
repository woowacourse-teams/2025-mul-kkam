package com.mulkkam.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mulkkam.domain.checker.ProgressChecker.Companion.KEY_OUTPUT_ACHIEVEMENT_RATE
import com.mulkkam.domain.repository.MembersRepository
import java.time.LocalDate

class ProgressWorker(
    appContext: Context,
    params: WorkerParameters,
    private val membersRepository: MembersRepository,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result =
        runCatching {
            membersRepository
                .getMembersProgressInfo(
                    LocalDate.now(),
                ).getOrError()
        }.fold(
            onSuccess = { membersProgressInfo ->
                val outputData =
                    workDataOf(
                        KEY_OUTPUT_ACHIEVEMENT_RATE to membersProgressInfo.achievementRate,
                    )
                Result.success(outputData)
            },
            onFailure = {
                Result.failure()
            },
        )
}
