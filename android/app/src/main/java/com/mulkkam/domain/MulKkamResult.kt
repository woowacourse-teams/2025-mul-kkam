package com.mulkkam.domain

import MulKkamError

data class MulKkamResult<T>(
    val error: MulKkamError? = null,
    val data: T? = null,
) {
    val isSuccess: Boolean
        get() = error == null
}
