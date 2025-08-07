package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.AddCupRequest
import com.mulkkam.data.remote.model.request.CupsRankRequest
import com.mulkkam.data.remote.model.request.PatchCupRequest
import com.mulkkam.data.remote.model.response.CupsRankResponse
import com.mulkkam.data.remote.model.response.CupsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CupsService {
    @GET("/cups")
    suspend fun getCups(): Result<CupsResponse>

    @POST("/cups")
    suspend fun postCups(
        @Body addCupRequest: AddCupRequest,
    ): Result<Unit>

    @PUT("/cups/ranks")
    suspend fun putCupsRank(
        @Body cupsRankRequest: CupsRankRequest,
    ): Result<CupsRankResponse>

    @PATCH("/cups/{cupId}")
    suspend fun patchCup(
        @Path("cupId") cupId: Long,
        @Body patchCupRequest: PatchCupRequest,
    ): Result<Unit>
}
