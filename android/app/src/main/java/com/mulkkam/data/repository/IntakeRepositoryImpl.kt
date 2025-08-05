package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.request.IntakeAmountRequest
import com.mulkkam.data.remote.model.request.IntakeHistoryRequest
import com.mulkkam.data.remote.model.response.toDomain
import com.mulkkam.data.remote.service.IntakeService
import com.mulkkam.domain.IntakeHistorySummaries
import com.mulkkam.domain.MulKkamError
import com.mulkkam.domain.MulKkamResult
import com.mulkkam.domain.repository.IntakeRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class IntakeRepositoryImpl(
    private val intakeService: IntakeService,
) : IntakeRepository {
    override suspend fun getIntakeHistory(
        from: LocalDate?,
        to: LocalDate?,
    ): MulKkamResult<IntakeHistorySummaries> {
        val result = intakeService.getIntakeHistory(dateToString(from), dateToString(to))
        return result.fold(
            onSuccess = {
                MulKkamResult(data = IntakeHistorySummaries(it.map { it.toDomain() }))
            },
            onFailure = { MulKkamResult(error = it as MulKkamError) },
        )
    }

    override suspend fun postIntakeHistory(
        dateTime: LocalDateTime,
        amount: Int,
    ) {
        intakeService.postIntakeHistory(IntakeHistoryRequest(dateTime.toString(), amount))
    }

    private fun dateToString(date: LocalDate?) = date?.format(formatter)

    override suspend fun patchIntakeTarget(amount: Int) {
        intakeService.patchIntakeTarget(IntakeAmountRequest(amount))
    }

    override suspend fun getIntakeTarget(): MulKkamResult<Int> {
        val result = intakeService.getIntakeTarget()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.amount) },
            onFailure = { MulKkamResult(error = it as MulKkamError) },
        )
    }

    companion object {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
}
