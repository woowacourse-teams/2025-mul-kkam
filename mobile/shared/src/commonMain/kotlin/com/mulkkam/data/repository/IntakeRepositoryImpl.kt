package com.mulkkam.data.repository

import com.mulkkam.data.remote.datasource.IntakeRemoteDataSource
import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.request.intake.IntakeAmountRequest
import com.mulkkam.data.remote.model.request.intake.IntakeHistoryCupRequest
import com.mulkkam.data.remote.model.request.intake.IntakeHistoryInputRequest
import com.mulkkam.data.remote.model.response.intake.toDomain
import com.mulkkam.domain.model.Gender
import com.mulkkam.domain.model.IntakeType
import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.intake.AchievementRate
import com.mulkkam.domain.model.intake.IntakeHistoryResult
import com.mulkkam.domain.model.intake.IntakeHistorySummaries
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.IntakeRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

class IntakeRepositoryImpl(
    private val intakeRemoteDataSource: IntakeRemoteDataSource,
) : IntakeRepository {
    override suspend fun getIntakeHistory(
        from: LocalDate,
        to: LocalDate,
    ): MulKkamResult<IntakeHistorySummaries> {
        val result = intakeRemoteDataSource.getIntakeHistory(from.toString(), to.toString())
        return result.fold(
            onSuccess = { intakeHistorySummary ->
                MulKkamResult(data = IntakeHistorySummaries(intakeHistorySummary.map { it.toDomain() }))
            },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getAchievementRates(
        from: LocalDate,
        to: LocalDate,
    ): MulKkamResult<List<AchievementRate>> {
        val result = intakeRemoteDataSource.getAchievementRates(from.toString(), to.toString())
        return result.fold(
            onSuccess = { response -> MulKkamResult(data = response.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun postIntakeHistoryInput(
        dateTime: LocalDateTime,
        intakeType: IntakeType,
        amount: CupAmount,
    ): MulKkamResult<IntakeHistoryResult> {
        val result =
            intakeRemoteDataSource.postIntakeHistoryInput(
                IntakeHistoryInputRequest(
                    dateTime.toString(),
                    intakeType.name,
                    amount.value,
                ),
            )
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun postIntakeHistoryCup(
        dateTime: LocalDateTime,
        cupId: Long,
    ): MulKkamResult<IntakeHistoryResult> {
        val result =
            intakeRemoteDataSource.postIntakeHistoryCup(
                IntakeHistoryCupRequest(
                    dateTime.toString(),
                    cupId,
                ),
            )
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun patchIntakeTarget(amount: Int): MulKkamResult<Unit> {
        val result = intakeRemoteDataSource.patchIntakeTarget(IntakeAmountRequest(amount))
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getIntakeTarget(): MulKkamResult<Int> {
        val result = intakeRemoteDataSource.getIntakeTarget()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.amount) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getIntakeAmountRecommended(): MulKkamResult<Int> {
        val result = intakeRemoteDataSource.getIntakeAmountRecommended()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.amount) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getIntakeAmountTargetRecommended(
        gender: Gender?,
        weight: BioWeight?,
    ): MulKkamResult<Int> {
        val result =
            intakeRemoteDataSource.getIntakeAmountTargetRecommended(
                gender?.name,
                weight?.value?.toDouble(),
            )
        return result.fold(
            onSuccess = { MulKkamResult(data = it.amount) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun deleteIntakeHistoryDetails(id: Int): MulKkamResult<Unit> {
        val result = intakeRemoteDataSource.deleteIntakeHistoryDetails(id)
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }
}
