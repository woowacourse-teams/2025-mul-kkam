package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.response.toDomain
import com.mulkkam.data.remote.service.CupsService
import com.mulkkam.domain.Cups

class CupsRepository(
    private val cupsService: CupsService,
) {
    suspend fun getCups(): Cups {
        val response = cupsService.getCups()
        return response.toDomain()
    }
}
