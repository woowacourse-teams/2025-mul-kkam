package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.MemberNicknameRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface MembersService {
    @GET("/members/nickname/validation")
    suspend fun getMembersNicknameValidation(
        @Query("nickname") nickname: String,
    ): Result<Unit>

    @PATCH("/members/nickname")
    suspend fun patchMembersNickname(
        @Body nickname: MemberNicknameRequest,
    ): Result<Unit>
}
