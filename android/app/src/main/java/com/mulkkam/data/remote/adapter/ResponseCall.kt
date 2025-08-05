package com.mulkkam.data.remote.adapter

import com.mulkkam.data.remote.model.error.ResponseError
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.response.ErrorResponse
import kotlinx.serialization.json.Json
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResponseCall<T>(
    private val call: Call<T>,
) : Call<Result<T>> {
    override fun enqueue(callback: Callback<Result<T>>) {
        call.enqueue(
            object : Callback<T> {
                override fun onResponse(
                    call: Call<T>,
                    response: Response<T>,
                ) {
                    val result = parseResponse(response)
                    callback.onResponse(this@ResponseCall, Response.success(result))
                }

                override fun onFailure(
                    call: Call<T>,
                    t: Throwable,
                ) {
                    callback.onResponse(this@ResponseCall, Response.success(Result.failure(t.toResponseError())))
                }
            },
        )
    }

    private fun parseResponse(response: Response<T>): Result<T> =
        runCatching {
            val body = response.body()
            val errorCode = parseErrorCode(response.errorBody())
            if (errorCode != null) {
                throw ResponseError.from(errorCode) ?: ResponseError.Unknown
            }

            body ?: throw NullPointerException("Response body is null")
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) },
        )

    private fun parseErrorCode(errorBody: ResponseBody?): String? =
        errorBody?.let {
            runCatching {
                Json.decodeFromString<ErrorResponse>(it.string()).code
            }.getOrNull()
        }

    override fun clone(): Call<Result<T>> = ResponseCall(call.clone())

    override fun execute(): Response<Result<T>> = throw UnsupportedOperationException("Use enqueue() instead")

    override fun isExecuted(): Boolean = call.isExecuted

    override fun cancel() = call.cancel()

    override fun isCanceled(): Boolean = call.isCanceled

    override fun request(): Request = call.request()

    override fun timeout(): Timeout = call.timeout()
}
