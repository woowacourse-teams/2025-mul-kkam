package com.mulkkam.data.remote.service

import retrofit2.http.GET
import retrofit2.http.Query

interface NicknameService {
    @GET("/nickname/validation")
    suspend fun getNicknameValidation(
        @Query("nickname") nickname: String,
    ): Result<Unit>
}
