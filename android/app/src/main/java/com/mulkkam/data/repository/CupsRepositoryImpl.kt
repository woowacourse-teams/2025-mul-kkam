package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
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
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun postCup(
        cupAmount: Int,
        cupNickname: String,
    ): MulKkamResult<Unit> {
        val result =
            cupsService.postCups(
                AddCupRequest(cupAmount, cupNickname),
            )
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }
}
