package com.mulkkam.data.repository

import MulKkamError
import com.mulkkam.data.remote.model.request.AddCupRequest
import com.mulkkam.data.remote.model.response.toDomain
import com.mulkkam.data.remote.service.CupsService
import com.mulkkam.domain.Cups
import com.mulkkam.domain.MulKkamResult
import com.mulkkam.domain.repository.CupsRepository

class CupsRepositoryImpl(
    private val cupsService: CupsService,
) : CupsRepository {
    override suspend fun getCups(): MulKkamResult<Cups> {
        val result = cupsService.getCups()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it as MulKkamError) },
        )
    }

    override suspend fun postCup(
        cupAmount: Int,
        cupNickname: String,
    ) {
        cupsService.postCups(
            AddCupRequest(cupAmount, cupNickname),
        )
    }
}
