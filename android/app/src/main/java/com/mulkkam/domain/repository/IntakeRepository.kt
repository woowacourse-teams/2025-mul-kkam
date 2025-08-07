package com.mulkkam.domain.repository

import com.mulkkam.domain.IntakeHistorySummaries
import com.mulkkam.domain.MulKkamResult
import com.mulkkam.domain.model.IntakeHistoryResult
import java.time.LocalDate
import java.time.LocalDateTime

interface IntakeRepository {
    suspend fun getIntakeHistory(
        from: LocalDate,
        to: LocalDate,
    ): MulKkamResult<IntakeHistorySummaries>

    suspend fun postIntakeHistory(
        dateTime: LocalDateTime,
        amount: Int,
    ): MulKkamResult<IntakeHistoryResult>

    suspend fun patchIntakeTarget(amount: Int): MulKkamResult<Unit>

    suspend fun getIntakeTarget(): MulKkamResult<Int>
}
