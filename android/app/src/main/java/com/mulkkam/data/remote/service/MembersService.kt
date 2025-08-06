package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.MemberNicknameRequest
import com.mulkkam.data.remote.model.request.MembersPhysicalAtrributesRequest
import com.mulkkam.data.remote.model.response.MemberNicknameResponse
import com.mulkkam.data.remote.model.response.MembersResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MembersService {
    @GET("/members/nickname/validation")
    suspend fun getMembersNicknameValidation(
        @Query("nickname") nickname: String,
    ): Result<Unit>

    @GET("/members/nickname")
    suspend fun getMembersNickname(): Result<MemberNicknameResponse>

    @PATCH("/members/nickname")
    suspend fun patchMembersNickname(
        @Body nickname: MemberNicknameRequest,
    ): Result<Unit>

    @GET("/members/{memberId}")
    suspend fun getMembers(
        @Path("memberId") memberId: Int,
    ): Result<MembersResponse>

    @POST("/members/physical-attributes")
    suspend fun postMembersPhysicalAttributes(
        @Body physicalAttributes: MembersPhysicalAtrributesRequest,
    ): Result<Unit>
}
