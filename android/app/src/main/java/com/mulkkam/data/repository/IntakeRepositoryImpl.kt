package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.IntakeAmountRequest
import com.mulkkam.data.remote.model.request.IntakeHistoryRequest
import com.mulkkam.data.remote.model.response.toDomain
import com.mulkkam.data.remote.service.IntakeService
import com.mulkkam.domain.Gender
import com.mulkkam.domain.IntakeHistorySummaries
import com.mulkkam.domain.MulKkamResult
import com.mulkkam.domain.model.IntakeHistoryResult
import com.mulkkam.domain.repository.IntakeRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class IntakeRepositoryImpl(
    private val intakeService: IntakeService,
) : IntakeRepository {
    override suspend fun getIntakeHistory(
        from: LocalDate,
        to: LocalDate,
    ): MulKkamResult<IntakeHistorySummaries> {
        val result = intakeService.getIntakeHistory(dateToString(from), dateToString(to))
        return result.fold(
            onSuccess = {
                MulKkamResult(data = IntakeHistorySummaries(it.map { it.toDomain() }))
            },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun postIntakeHistory(
        dateTime: LocalDateTime,
        amount: Int,
    ): MulKkamResult<IntakeHistoryResult> {
        val result =
            intakeService.postIntakeHistory(IntakeHistoryRequest(dateTime.toString(), amount))
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    private fun dateToString(date: LocalDate) = date.format(formatter)

    override suspend fun patchIntakeTarget(amount: Int): MulKkamResult<Unit> {
        val result = intakeService.patchIntakeTarget(IntakeAmountRequest(amount))
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun getIntakeTarget(): MulKkamResult<Int> {
        val result = intakeService.getIntakeTarget()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.amount) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun getIntakeAmountRecommended(): MulKkamResult<Int> {
        val result = intakeService.getIntakeAmountRecommended()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.amount) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun getIntakeAmountTargetRecommended(
        gender: Gender?,
        weight: Int?,
    ): MulKkamResult<Int> {
        val result =
            intakeService.getIntakeAmountTargetRecommended(
                gender?.name,
                weight?.toDouble(),
            )
        return result.fold(
            onSuccess = { MulKkamResult(data = it.amount) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    companion object {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
}
