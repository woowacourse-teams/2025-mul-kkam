package com.mulkkam.domain.repository

import com.mulkkam.domain.MulKkamResult
import com.mulkkam.domain.model.Cups

interface CupsRepository {
    suspend fun getCups(): MulKkamResult<Cups>

    suspend fun postCup(
        cupAmount: Int,
        cupNickname: String,
    ): MulKkamResult<Unit>
}
