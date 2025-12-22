package com.mulkkam.data.checker

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.mulkkam.data.work.DrinkByAmountWorker
import com.mulkkam.data.work.IntakeWorker
import com.mulkkam.domain.checker.IntakeChecker

class IntakeCheckerImpl(
    private val workManager: WorkManager,
) : IntakeChecker {
    override fun drink(cupId: Long): String {
        val request =
            OneTimeWorkRequestBuilder<DrinkByAmountWorker>()
                .setInputData(workDataOf(IntakeChecker.KEY_INTAKE_CHECKER_CUP_ID to cupId))
                .build()
        workManager.enqueue(request)
        return request.id.toString()
    }

    override fun checkWidgetInfo(): String {
        val request = OneTimeWorkRequestBuilder<IntakeWorker>().build()
        workManager.enqueue(request)
        return request.id.toString()
    }
}
