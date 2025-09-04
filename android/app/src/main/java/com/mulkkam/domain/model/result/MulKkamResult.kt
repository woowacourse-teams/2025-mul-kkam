package com.mulkkam.domain.model.result

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError

data class MulKkamResult<T>(
    val error: MulKkamError? = null,
    val data: T? = null,
) {
    @Suppress("UNCHECKED_CAST")
    fun getOrError(): T =
        when {
            error != null -> throw error
            data != null -> data
            else -> null as T
        }
}

fun <T> Result<T>.toMulKkamResult(): MulKkamResult<T> =
    fold(
        onSuccess = { MulKkamResult(data = it) },
        onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
    )

fun Throwable.toMulKkamError(): MulKkamError =
    when (this) {
        is MulKkamError -> this
        else -> MulKkamError.Unknown
    }
