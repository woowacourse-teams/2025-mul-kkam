package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.toAddCupRequest
import com.mulkkam.data.remote.model.request.toData
import com.mulkkam.data.remote.model.request.toPatchCupRequest
import com.mulkkam.data.remote.model.response.toDomain
import com.mulkkam.data.remote.service.CupsService
import com.mulkkam.domain.MulKkamResult
import com.mulkkam.domain.model.Cup
import com.mulkkam.domain.model.Cups
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

    override suspend fun postCup(cup: Cup): MulKkamResult<Unit> {
        val result =
            cupsService.postCups(cup.toAddCupRequest())
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun putCupsRank(cups: Cups): MulKkamResult<Cups> {
        val request = cups.toData()
        val result = cupsService.putCupsRank(request)
        return result.fold(
            onSuccess = { MulKkamResult(data = cups) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun patchCup(cup: Cup): MulKkamResult<Unit> {
        val result =
            cupsService.patchCup(
                cupId = cup.id,
                patchCupRequest = cup.toPatchCupRequest(),
            )
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }
}
