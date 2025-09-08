package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.cups.CupsRankRequest
import com.mulkkam.data.remote.model.request.cups.NewCupRequest
import com.mulkkam.data.remote.model.response.cups.CupEmojisResponse
import com.mulkkam.data.remote.model.response.cups.CupsDefaultResponse
import com.mulkkam.data.remote.model.response.cups.CupsRankResponse
import com.mulkkam.data.remote.model.response.cups.CupsResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CupsService {
    @GET("/cups")
    suspend fun getCups(): Result<CupsResponse>

    @GET("/cups/default")
    suspend fun getCupsDefault(): Result<CupsDefaultResponse>

    @POST("/cups")
    suspend fun postCup(
        @Body newCupRequest: NewCupRequest,
    ): Result<Unit>

    @PUT("/cups/ranks")
    suspend fun putCupsRank(
        @Body cupsRankRequest: CupsRankRequest,
    ): Result<CupsRankResponse>

    @PATCH("/cups/{cupId}")
    suspend fun patchCup(
        @Path("cupId") cupId: Long,
        @Body newCupRequest: NewCupRequest,
    ): Result<Unit>

    @DELETE("/cups/{id}")
    suspend fun deleteCup(
        @Path("id") id: Long,
    ): Result<Unit>

    @GET("/cup-emoji")
    suspend fun getCupEmojis(): Result<CupEmojisResponse>

    @PUT("/cups/reset")
    suspend fun resetCups(): Result<Unit>
}
