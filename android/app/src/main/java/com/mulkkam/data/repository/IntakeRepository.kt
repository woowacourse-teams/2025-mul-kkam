package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.request.IntakeAmountRequest
import com.mulkkam.data.remote.model.request.IntakeHistoryRequest
import com.mulkkam.data.remote.model.response.toDomain
import com.mulkkam.data.remote.service.IntakeService
import com.mulkkam.domain.IntakeHistorySummary
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class IntakeRepository(
    private val intakeService: IntakeService,
) {
    suspend fun getIntakeHistory(
        from: LocalDate?,
        to: LocalDate?,
    ): List<IntakeHistorySummary> {
        val result = intakeService.getIntakeHistory(dateToString(from), dateToString(to))
        return result.map { it.toDomain() }
    }

    suspend fun postIntakeHistory(
        dateTime: LocalDateTime,
        amount: Int,
    ) {
        intakeService.postIntakeHistory(IntakeHistoryRequest(dateTime.toString(), amount))
    }

    private fun dateToString(date: LocalDate?) = date?.format(formatter)

    suspend fun patchIntakeTarget(amount: Int) {
        intakeService.patchIntakeTarget(IntakeAmountRequest(amount))
    }

    suspend fun getIntakeTarget(): Int {
        val result = intakeService.getIntakeTarget()
        return result.amount
    }

    companion object {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
}
