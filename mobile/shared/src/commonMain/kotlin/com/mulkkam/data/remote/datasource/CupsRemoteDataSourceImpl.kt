package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.api.safeApiCall
import com.mulkkam.data.remote.api.safeApiCallUnit
import com.mulkkam.data.remote.model.request.cups.CupsRankRequest
import com.mulkkam.data.remote.model.request.cups.NewCupRequest
import com.mulkkam.data.remote.model.response.cups.CupEmojisResponse
import com.mulkkam.data.remote.model.response.cups.CupsRankResponse
import com.mulkkam.data.remote.model.response.cups.CupsResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class CupsRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : CupsRemoteDataSource {
    override suspend fun getCups(): Result<CupsResponse> =
        safeApiCall {
            httpClient.get("/cups")
        }

    override suspend fun getCupsDefault(): Result<CupsResponse> =
        safeApiCall {
            httpClient.get("/cups/default")
        }

    override suspend fun postCup(newCupRequest: NewCupRequest): Result<Unit> =
        safeApiCallUnit {
            httpClient.post("/cups") {
                setBody(newCupRequest)
            }
        }

    override suspend fun putCupsRank(cupsRankRequest: CupsRankRequest): Result<CupsRankResponse> =
        safeApiCall {
            httpClient.put("/cups/ranks") {
                setBody(cupsRankRequest)
            }
        }

    override suspend fun patchCup(
        cupId: Long,
        newCupRequest: NewCupRequest,
    ): Result<Unit> =
        safeApiCallUnit {
            httpClient.patch("/cups/$cupId") {
                setBody(newCupRequest)
            }
        }

    override suspend fun deleteCup(id: Long): Result<Unit> =
        safeApiCallUnit {
            httpClient.delete("/cups/$id")
        }

    override suspend fun getCupEmojis(): Result<CupEmojisResponse> =
        safeApiCall {
            httpClient.get("/cup-emoji")
        }

    override suspend fun resetCups(): Result<Unit> =
        safeApiCallUnit {
            httpClient.put("/cups/reset")
        }
}
