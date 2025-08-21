package com.mulkkam.domain.repository

import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.bio.Gender
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.intake.IntakeHistoryResult
import com.mulkkam.domain.model.intake.IntakeHistorySummaries
import com.mulkkam.domain.model.result.MulKkamResult
import java.time.LocalDate
import java.time.LocalDateTime

interface IntakeRepository {
    suspend fun getIntakeHistory(
        from: LocalDate,
        to: LocalDate,
    ): MulKkamResult<IntakeHistorySummaries>

    suspend fun postIntakeHistory(
        dateTime: LocalDateTime,
        amount: CupAmount,
    ): MulKkamResult<IntakeHistoryResult>

    suspend fun patchIntakeTarget(amount: Int): MulKkamResult<Unit>

    suspend fun getIntakeTarget(): MulKkamResult<Int>

    suspend fun getIntakeAmountRecommended(): MulKkamResult<Int>

    suspend fun getIntakeAmountTargetRecommended(
        gender: Gender?,
        weight: BioWeight?,
    ): MulKkamResult<Int>

    suspend fun deleteIntakeHistoryDetails(id: Int): MulKkamResult<Unit>
}
