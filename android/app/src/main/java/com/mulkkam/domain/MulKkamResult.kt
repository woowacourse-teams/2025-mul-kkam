package com.mulkkam.domain

data class MulKkamResult<T>(
    val error: MulKkamError? = null,
    val data: T? = null,
) {
    fun getOrError(): T =
        when {
            error != null -> throw error
            data != null -> data
            else -> Unit as T
        }
}
