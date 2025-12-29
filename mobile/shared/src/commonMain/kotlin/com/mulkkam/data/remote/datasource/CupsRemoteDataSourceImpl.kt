package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.request.cups.CupsRankRequest
import com.mulkkam.data.remote.model.request.cups.NewCupRequest
import com.mulkkam.data.remote.model.response.cups.CupEmojisResponse
import com.mulkkam.data.remote.model.response.cups.CupsRankResponse
import com.mulkkam.data.remote.model.response.cups.CupsResponse

// TODO: DataSource 구현 필요
class CupsRemoteDataSourceImpl : CupsRemoteDataSource {
    override suspend fun getCups(): Result<CupsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getCupsDefault(): Result<CupsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun postCup(newCupRequest: NewCupRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun putCupsRank(cupsRankRequest: CupsRankRequest): Result<CupsRankResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun patchCup(
        cupId: Long,
        newCupRequest: NewCupRequest,
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCup(id: Long): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getCupEmojis(): Result<CupEmojisResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun resetCups(): Result<Unit> {
        TODO("Not yet implemented")
    }
}
