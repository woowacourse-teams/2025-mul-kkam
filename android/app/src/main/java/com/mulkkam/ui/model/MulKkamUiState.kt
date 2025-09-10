package com.mulkkam.ui.model

import com.mulkkam.domain.model.result.MulKkamError

sealed class MulKkamUiState<out T> {
    data class Success<T>(
        val data: T,
    ) : MulKkamUiState<T>()

    data object Idle : MulKkamUiState<Nothing>()

    data object Loading : MulKkamUiState<Nothing>()

    data class Failure(
        val error: MulKkamError,
    ) : MulKkamUiState<Nothing>()

    inline fun <reified T> MulKkamUiState<T>.toSuccessDataOrNull(): T? = (this as? Success<T>)?.data
}
