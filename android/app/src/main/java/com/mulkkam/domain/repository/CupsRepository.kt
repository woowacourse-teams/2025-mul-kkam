package com.mulkkam.domain.repository

import com.mulkkam.domain.Cups
import com.mulkkam.domain.MulKkamResult

interface CupsRepository {
    suspend fun getCups(): MulKkamResult<Cups>

    suspend fun postCup(
        cupAmount: Int,
        cupNickname: String,
    )
}
