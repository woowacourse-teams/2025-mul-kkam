package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.request.AddCupRequest
import com.mulkkam.data.remote.service.CupsService

class CupsRepository(
    private val cupsService: CupsService,
) {
    suspend fun postCup(
        cupAmount: Int,
        cupNickname: String,
    ) {
        cupsService.postCups(
            AddCupRequest(cupAmount, cupNickname),
        )
    }
}
