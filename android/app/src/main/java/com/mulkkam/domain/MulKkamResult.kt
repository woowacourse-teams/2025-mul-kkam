package com.mulkkam.domain

data class MulKkamResult<T>(
    val error: MulKkamError? = null,
    val data: T? = null,
) {
    val isSuccess: Boolean
        get() = error == null
}
