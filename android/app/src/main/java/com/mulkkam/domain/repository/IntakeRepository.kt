package com.mulkkam.domain.repository

import com.mulkkam.domain.IntakeHistorySummaries
import com.mulkkam.domain.MulKkamResult
import java.time.LocalDate
import java.time.LocalDateTime

interface IntakeRepository {
    suspend fun getIntakeHistory(
        from: LocalDate?,
        to: LocalDate?,
    ): MulKkamResult<IntakeHistorySummaries>

    suspend fun postIntakeHistory(
        dateTime: LocalDateTime,
        amount: Int,
    )

    suspend fun patchIntakeTarget(amount: Int)

    suspend fun getIntakeTarget(): MulKkamResult<Int>
}
