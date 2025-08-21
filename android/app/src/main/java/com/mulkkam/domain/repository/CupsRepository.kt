package com.mulkkam.domain.repository

import com.mulkkam.domain.model.cups.Cup
import com.mulkkam.domain.model.cups.CupEmoji
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.result.MulKkamResult

interface CupsRepository {
    suspend fun getCups(): MulKkamResult<Cups>

    suspend fun postCup(cup: Cup): MulKkamResult<Unit>

    suspend fun putCupsRank(cups: Cups): MulKkamResult<Cups>

    suspend fun patchCup(cup: Cup): MulKkamResult<Unit>

    suspend fun deleteCup(id: Long): MulKkamResult<Unit>

    suspend fun getCupEmojis(): MulKkamResult<List<CupEmoji>>
}
