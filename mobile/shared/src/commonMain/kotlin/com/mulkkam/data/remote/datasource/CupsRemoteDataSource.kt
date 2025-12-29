package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.request.cups.CupsRankRequest
import com.mulkkam.data.remote.model.request.cups.NewCupRequest
import com.mulkkam.data.remote.model.response.cups.CupEmojisResponse
import com.mulkkam.data.remote.model.response.cups.CupsRankResponse
import com.mulkkam.data.remote.model.response.cups.CupsResponse

interface CupsRemoteDataSource {
    suspend fun getCups(): Result<CupsResponse>

    suspend fun getCupsDefault(): Result<CupsResponse>

    suspend fun postCup(newCupRequest: NewCupRequest): Result<Unit>

    suspend fun putCupsRank(cupsRankRequest: CupsRankRequest): Result<CupsRankResponse>

    suspend fun patchCup(
        cupId: Long,
        newCupRequest: NewCupRequest,
    ): Result<Unit>

    suspend fun deleteCup(id: Long): Result<Unit>

    suspend fun getCupEmojis(): Result<CupEmojisResponse>

    suspend fun resetCups(): Result<Unit>
}
