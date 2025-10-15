package com.mulkkam.data.remote.adapter

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.Result

/**
 * Retrofit에서 Call<Result<T>> 형태의 응답을 처리하기 위한 CallAdapter.Factory 구현체입니다.
 * HTTP 응답을 Result<T>로 감싸 에러 처리와 성공 처리를 통일된 방식으로 할 수 있게 해줍니다.
 *
 * returnType이 Call<Result<T>> 형태일 때만 MulKkamCallAdapter를 반환합니다.
 */

class MulKkamCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? =
        when {
            // [1] 반환 타입이 Call<...> 형태인지 확인
            getRawType(returnType) != Call::class.java -> null
            returnType !is ParameterizedType -> null

            // [2] Call<Result<...>> 형태인지 확인
            getRawType(getParameterUpperBound(0, returnType)) != Result::class.java -> null
            getParameterUpperBound(0, returnType) !is ParameterizedType -> null

            // [3] Result<T>의 T 타입을 추출하여 어댑터 생성
            else -> {
                val innerType = getParameterUpperBound(0, returnType) as ParameterizedType
                val responseType = getParameterUpperBound(0, innerType)
                MulKkamCallAdapter<Any>(responseType)
            }
        }
}
