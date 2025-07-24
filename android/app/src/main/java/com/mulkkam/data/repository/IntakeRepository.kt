package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.response.toDomain
import com.mulkkam.data.remote.service.IntakeService
import com.mulkkam.domain.IntakeHistorySummary
import java.time.LocalDate
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

    private fun dateToString(date: LocalDate?) = date?.format(formatter)

    companion object {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
}
