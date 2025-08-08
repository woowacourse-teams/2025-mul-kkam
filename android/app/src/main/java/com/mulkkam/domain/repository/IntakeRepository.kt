package com.mulkkam.domain.repository

import com.mulkkam.domain.model.Gender
import com.mulkkam.domain.model.IntakeHistoryResult
import com.mulkkam.domain.model.IntakeHistorySummaries
import com.mulkkam.domain.model.MulKkamResult
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

    suspend fun getIntakeAmountRecommended(): MulKkamResult<Int>

    suspend fun getIntakeAmountTargetRecommended(
        gender: Gender?,
        weight: Int?,
    ): MulKkamResult<Int>

    suspend fun patchIntakeAmountTargetSuggested(amount: Int): MulKkamResult<Unit>

    suspend fun deleteIntakeHistoryDetails(id: Int): MulKkamResult<Unit>
}
