package com.mulkkam.domain.model.result

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

    val isSuccess: Boolean
        get() = error == null

    val isFailure: Boolean
        get() = error != null
}

fun Throwable.toMulKkamError(): MulKkamError =
    when (this) {
        is MulKkamError -> this
        else -> MulKkamError.Unknown
    }

/**
 * Result를 MulKkamResult로 변환하는 확장 함수
 */
fun <T> kotlin.Result<T>.toMulKkamResult(): MulKkamResult<T> =
    fold(
        onSuccess = { MulKkamResult(data = it) },
        onFailure = { MulKkamResult(error = it.toMulKkamError()) },
    )
