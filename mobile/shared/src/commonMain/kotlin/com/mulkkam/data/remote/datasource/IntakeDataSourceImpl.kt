package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.request.intake.IntakeAmountRequest
import com.mulkkam.data.remote.model.request.intake.IntakeHistoryCupRequest
import com.mulkkam.data.remote.model.request.intake.IntakeHistoryInputRequest
import com.mulkkam.data.remote.model.response.intake.IntakeHistoryResultResponse
import com.mulkkam.data.remote.model.response.intake.IntakeHistorySummaryResponse
import com.mulkkam.data.remote.model.response.intake.IntakeTargetAmountResponse
import com.mulkkam.data.remote.model.response.intake.ReadAchievementRatesResponse

// TODO: DataSource 구현 필요
class IntakeDataSourceImpl : IntakeDataSource {
    override suspend fun getIntakeHistory(
        from: String,
        to: String,
    ): Result<List<IntakeHistorySummaryResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAchievementRates(
        from: String,
        to: String,
    ): Result<ReadAchievementRatesResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun postIntakeHistoryInput(intakeHistory: IntakeHistoryInputRequest): Result<IntakeHistoryResultResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun postIntakeHistoryCup(intakeHistory: IntakeHistoryCupRequest): Result<IntakeHistoryResultResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun patchIntakeTarget(intakeAmount: IntakeAmountRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getIntakeTarget(): Result<IntakeTargetAmountResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getIntakeAmountRecommended(): Result<IntakeTargetAmountResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getIntakeAmountTargetRecommended(
        gender: String?,
        weight: Double?,
    ): Result<IntakeTargetAmountResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteIntakeHistoryDetails(id: Int): Result<Unit> {
        TODO("Not yet implemented")
    }
}
