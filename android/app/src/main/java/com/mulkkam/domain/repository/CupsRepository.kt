package com.mulkkam.domain.repository

import com.mulkkam.domain.MulKkamResult
import com.mulkkam.domain.model.Cup
import com.mulkkam.domain.model.Cups

interface CupsRepository {
    suspend fun getCups(): MulKkamResult<Cups>

    suspend fun postCup(cup: Cup): MulKkamResult<Unit>

    suspend fun putCupsRank(cups: Cups): MulKkamResult<Cups>

    suspend fun patchCup(cup: Cup): MulKkamResult<Unit>
}
