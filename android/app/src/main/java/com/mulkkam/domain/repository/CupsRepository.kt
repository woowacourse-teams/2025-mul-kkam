package com.mulkkam.domain.repository

import com.mulkkam.domain.Cups

interface CupsRepository {
    suspend fun getCups(): Cups

    suspend fun postCup(
        cupAmount: Int,
        cupNickname: String,
    )
}
