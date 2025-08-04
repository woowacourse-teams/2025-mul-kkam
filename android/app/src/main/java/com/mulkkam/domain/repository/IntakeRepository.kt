package com.mulkkam.domain.repository

import com.mulkkam.domain.IntakeHistorySummaries
import java.time.LocalDate
import java.time.LocalDateTime

interface IntakeRepository {
    suspend fun getIntakeHistory(
        from: LocalDate?,
        to: LocalDate?,
    ): IntakeHistorySummaries

    suspend fun postIntakeHistory(
        dateTime: LocalDateTime,
        amount: Int,
    )

    suspend fun patchIntakeTarget(amount: Int)

    suspend fun getIntakeTarget(): Int
}
