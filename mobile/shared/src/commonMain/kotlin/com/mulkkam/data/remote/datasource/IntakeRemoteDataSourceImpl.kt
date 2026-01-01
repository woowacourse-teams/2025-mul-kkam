package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.api.safeApiCall
import com.mulkkam.data.remote.api.safeApiCallUnit
import com.mulkkam.data.remote.model.request.intake.IntakeAmountRequest
import com.mulkkam.data.remote.model.request.intake.IntakeHistoryCupRequest
import com.mulkkam.data.remote.model.request.intake.IntakeHistoryInputRequest
import com.mulkkam.data.remote.model.response.intake.IntakeHistoryResultResponse
import com.mulkkam.data.remote.model.response.intake.IntakeHistorySummaryResponse
import com.mulkkam.data.remote.model.response.intake.IntakeTargetAmountResponse
import com.mulkkam.data.remote.model.response.intake.ReadAchievementRatesResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class IntakeRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : IntakeRemoteDataSource {
    override suspend fun getIntakeHistory(
        from: String,
        to: String,
    ): Result<List<IntakeHistorySummaryResponse>> =
        safeApiCall {
            httpClient.get("/intake/history") {
                url {
                    parameters.append("from", from)
                    parameters.append("to", to)
                }
            }
        }

    override suspend fun getAchievementRates(
        from: String,
        to: String,
    ): Result<ReadAchievementRatesResponse> =
        safeApiCall {
            httpClient.get("/intake/history/achievement-rates") {
                url {
                    parameters.append("from", from)
                    parameters.append("to", to)
                }
            }
        }

    override suspend fun postIntakeHistoryInput(intakeHistory: IntakeHistoryInputRequest): Result<IntakeHistoryResultResponse> =
        safeApiCall {
            httpClient.post("/intake/history/input") {
                setBody(intakeHistory)
            }
        }

    override suspend fun postIntakeHistoryCup(intakeHistory: IntakeHistoryCupRequest): Result<IntakeHistoryResultResponse> =
        safeApiCall {
            httpClient.post("/intake/history/cup") {
                setBody(intakeHistory)
            }
        }

    override suspend fun patchIntakeTarget(intakeAmount: IntakeAmountRequest): Result<Unit> =
        safeApiCallUnit {
            httpClient.patch("/intake/amount/target") {
                setBody(intakeAmount)
            }
        }

    override suspend fun getIntakeTarget(): Result<IntakeTargetAmountResponse> =
        safeApiCall {
            httpClient.get("/intake/amount/target")
        }

    override suspend fun getIntakeAmountRecommended(): Result<IntakeTargetAmountResponse> =
        safeApiCall {
            httpClient.get("/intake/amount/recommended")
        }

    override suspend fun getIntakeAmountTargetRecommended(
        gender: String?,
        weight: Double?,
    ): Result<IntakeTargetAmountResponse> =
        safeApiCall {
            httpClient.get("/intake/amount/target/recommended") {
                url {
                    gender?.let { parameters.append("gender", it) }
                    weight?.let { parameters.append("weight", it.toString()) }
                }
            }
        }

    override suspend fun deleteIntakeHistoryDetails(id: Int): Result<Unit> =
        safeApiCallUnit {
            httpClient.delete("/intake/history/details/$id")
        }
}
