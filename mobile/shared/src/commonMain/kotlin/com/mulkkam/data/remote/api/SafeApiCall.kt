package com.mulkkam.data.remote.api

import com.mulkkam.data.remote.model.error.ErrorResponse
import com.mulkkam.data.remote.model.error.ResponseError
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

suspend inline fun <reified T> safeApiCall(crossinline call: suspend () -> HttpResponse): Result<T> =
    try {
        val response = call()
        if (response.status.isSuccess()) {
            Result.success(response.body<T>())
        } else {
            val errorResponse = runCatching { response.body<ErrorResponse>() }.getOrNull()
            val errorCode = errorResponse?.error ?: errorResponse?.code
            val responseError: ResponseError = ResponseError.from(errorCode)
            Result.failure(responseError)
        }
    } catch (e: Exception) {
        val error: ResponseError =
            when {
                e.message?.contains("Unable to resolve host") == true -> ResponseError.NetworkUnavailable
                e.message?.contains("timeout") == true -> ResponseError.NetworkUnavailable
                e.message?.contains("Connect") == true -> ResponseError.NetworkUnavailable
                else -> ResponseError.Unknown
            }
        Result.failure(error)
    }

suspend inline fun safeApiCallUnit(crossinline call: suspend () -> HttpResponse): Result<Unit> =
    try {
        val response = call()
        if (response.status.isSuccess()) {
            Result.success(Unit)
        } else {
            val errorResponse = runCatching { response.body<ErrorResponse>() }.getOrNull()
            val errorCode = errorResponse?.error ?: errorResponse?.code
            val responseError: ResponseError = ResponseError.from(errorCode)
            Result.failure(responseError)
        }
    } catch (e: Exception) {
        val error: ResponseError =
            when {
                e.message?.contains("Unable to resolve host") == true -> ResponseError.NetworkUnavailable
                e.message?.contains("timeout") == true -> ResponseError.NetworkUnavailable
                e.message?.contains("Connect") == true -> ResponseError.NetworkUnavailable
                else -> ResponseError.Unknown
            }
        Result.failure(error)
    }
