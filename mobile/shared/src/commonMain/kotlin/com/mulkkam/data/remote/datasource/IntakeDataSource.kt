package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.request.intake.IntakeAmountRequest
import com.mulkkam.data.remote.model.request.intake.IntakeHistoryCupRequest
import com.mulkkam.data.remote.model.request.intake.IntakeHistoryInputRequest
import com.mulkkam.data.remote.model.response.intake.IntakeHistoryResultResponse
import com.mulkkam.data.remote.model.response.intake.IntakeHistorySummaryResponse
import com.mulkkam.data.remote.model.response.intake.IntakeTargetAmountResponse
import com.mulkkam.data.remote.model.response.intake.ReadAchievementRatesResponse

interface IntakeDataSource {
    suspend fun getIntakeHistory(
        from: String,
        to: String,
    ): Result<List<IntakeHistorySummaryResponse>>

    suspend fun getAchievementRates(
        from: String,
        to: String,
    ): Result<ReadAchievementRatesResponse>

    suspend fun postIntakeHistoryInput(intakeHistory: IntakeHistoryInputRequest): Result<IntakeHistoryResultResponse>

    suspend fun postIntakeHistoryCup(intakeHistory: IntakeHistoryCupRequest): Result<IntakeHistoryResultResponse>

    suspend fun patchIntakeTarget(intakeAmount: IntakeAmountRequest): Result<Unit>

    suspend fun getIntakeTarget(): Result<IntakeTargetAmountResponse>

    suspend fun getIntakeAmountRecommended(): Result<IntakeTargetAmountResponse>

    suspend fun getIntakeAmountTargetRecommended(
        gender: String?,
        weight: Double?,
    ): Result<IntakeTargetAmountResponse>

    suspend fun deleteIntakeHistoryDetails(id: Int): Result<Unit>
}
