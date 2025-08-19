package com.mulkkam.data.local.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mulkkam.domain.repository.MembersRepository
import java.time.LocalDate

class ProgressCheckWorker(
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

    companion object {
        const val KEY_OUTPUT_ACHIEVEMENT_RATE = "ACHIEVEMENT_RATE"
    }
}
