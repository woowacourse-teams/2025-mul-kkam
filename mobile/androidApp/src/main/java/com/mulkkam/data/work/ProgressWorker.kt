package com.mulkkam.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mulkkam.domain.checker.ProgressChecker.Companion.KEY_PROGRESS_CHECKER_ACHIEVEMENT_RATE
import com.mulkkam.domain.repository.MembersRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

@HiltWorker
class ProgressWorker
    @AssistedInject
    constructor(
        @Assisted appContext: Context,
        @Assisted params: WorkerParameters,
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
                            KEY_PROGRESS_CHECKER_ACHIEVEMENT_RATE to membersProgressInfo.achievementRate,
                        )
                    Result.success(outputData)
                },
                onFailure = {
                    Result.failure()
                },
            )
    }
