package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.request.AddCupRequest
import com.mulkkam.data.remote.model.response.toDomain
import com.mulkkam.data.remote.service.CupsService
import com.mulkkam.domain.Cups
import com.mulkkam.domain.repository.CupsRepository

class CupsRepositoryImpl(
    private val cupsService: CupsService,
) : CupsRepository {
    override suspend fun getCups(): Cups = cupsService.getCups().toDomain()

    override suspend fun postCup(
        cupAmount: Int,
        cupNickname: String,
    ) {
        cupsService.postCups(
            AddCupRequest(cupAmount, cupNickname),
        )
    }
}
