package com.mulkkam.data.remote.api

import com.mulkkam.data.remote.model.error.ErrorResponse
import com.mulkkam.data.remote.model.error.ResponseError
import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.MulKkamResult
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

suspend inline fun <reified T> safeApiCall(crossinline call: suspend () -> HttpResponse): MulKkamResult<T> =
    try {
        val response = call()
        if (response.status.isSuccess()) {
            MulKkamResult(data = response.body<T>())
        } else {
            val errorResponse = runCatching { response.body<ErrorResponse>() }.getOrNull()
            val errorCode = errorResponse?.error ?: errorResponse?.code
            val responseError = ResponseError.from(errorCode)
            MulKkamResult(error = responseError.toDomain())
        }
    } catch (e: Exception) {
        val error: MulKkamError =
            when {
                e.message?.contains("Unable to resolve host") == true -> MulKkamError.NetworkUnavailable
                e.message?.contains("timeout") == true -> MulKkamError.NetworkUnavailable
                else -> MulKkamError.Unknown
            }
        MulKkamResult(error = error)
    }

suspend inline fun safeApiCallUnit(crossinline call: suspend () -> HttpResponse): MulKkamResult<Unit> =
    try {
        val response = call()
        if (response.status.isSuccess()) {
            MulKkamResult(data = Unit)
        } else {
            val errorResponse = runCatching { response.body<ErrorResponse>() }.getOrNull()
            val errorCode = errorResponse?.error ?: errorResponse?.code
            val responseError = ResponseError.from(errorCode)
            MulKkamResult(error = responseError.toDomain())
        }
    } catch (e: Exception) {
        val error: MulKkamError =
            when {
                e.message?.contains("Unable to resolve host") == true -> MulKkamError.NetworkUnavailable
                e.message?.contains("timeout") == true -> MulKkamError.NetworkUnavailable
                else -> MulKkamError.Unknown
            }
        MulKkamResult(error = error)
    }
