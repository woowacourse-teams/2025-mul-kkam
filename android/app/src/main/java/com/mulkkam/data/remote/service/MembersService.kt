package com.mulkkam.data.remote.service

import retrofit2.http.GET
import retrofit2.http.Query

interface MembersService {
    @GET("/members/nickname/validation")
    suspend fun getMembersNicknameValidation(
        @Query("nickname") nickname: String,
    ): Result<Unit>
}
