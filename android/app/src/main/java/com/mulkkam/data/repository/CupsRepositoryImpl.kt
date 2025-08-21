package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.cups.toData
import com.mulkkam.data.remote.model.request.cups.toNewCupRequest
import com.mulkkam.data.remote.model.request.cups.toPatchCupRequest
import com.mulkkam.data.remote.model.response.cups.toDomain
import com.mulkkam.data.remote.service.CupsService
import com.mulkkam.domain.model.cups.Cup
import com.mulkkam.domain.model.cups.CupEmoji
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.result.MulKkamResult
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
            cupsService.postCup(cup.toNewCupRequest())
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

    override suspend fun deleteCup(id: Long): MulKkamResult<Unit> {
        val result = cupsService.deleteCup(id)
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun getCupEmojis(): MulKkamResult<List<CupEmoji>> {
        val result = cupsService.getCupEmojis()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }
}
