package com.mulkkam.data.remote

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class MulKkamCallAdapter<T>(
    private val responseType: Type,
) : CallAdapter<T, Call<Result<T>>> {
    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): Call<Result<T>> = ResponseCall(call)
}
