package com.mulkkam.data.local.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.ui.widget.ProgressWidget
import java.time.LocalDate

class ProgressWidgetWorker(
    private val appContext: Context,
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
                sendResultBroadcast(membersProgressInfo.achievementRate)
                Result.success()
            },
            onFailure = {
                Result.failure()
            },
        )

    private fun sendResultBroadcast(rate: Float) {
        val intent = ProgressWidget.newIntent(appContext, rate)
        appContext.sendBroadcast(intent)
    }
}
