package com.mulkkam.data.remote.adapter

import com.mulkkam.data.remote.model.response.ErrorResponse
import com.mulkkam.domain.MulKkamError
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
                    callback.onResponse(this@ResponseCall, Response.success(Result.failure(t)))
                }
            },
        )
    }

    private fun parseResponse(response: Response<T>): Result<T> {
        return try {
            val body = response.body()
            val errorCode = parseErrorCode(response.errorBody())
            if (errorCode != null) {
                return Result.failure(MulKkamError.from(errorCode) ?: MulKkamError.Unknown)
            }

            when (body) {
                null -> {
                    Result.failure(NullPointerException("Response body is null"))
                }

                else -> Result.success(body)
            }
        } catch (e: Exception) {
            Result.failure(MulKkamError.Unknown)
        }
    }

    private fun parseErrorCode(errorBody: ResponseBody?): String? =
        errorBody?.let {
            try {
                val errorResponse = Json.decodeFromString<ErrorResponse>(it.string())
                errorResponse.code
            } catch (e: Exception) {
                null
            }
        }

    override fun clone(): Call<Result<T>> = ResponseCall(call.clone())

    override fun execute(): Response<Result<T>> = throw UnsupportedOperationException("Use enqueue() instead")

    override fun isExecuted(): Boolean = call.isExecuted

    override fun cancel() = call.cancel()

    override fun isCanceled(): Boolean = call.isCanceled

    override fun request(): Request = call.request()

    override fun timeout(): Timeout = call.timeout()
}
