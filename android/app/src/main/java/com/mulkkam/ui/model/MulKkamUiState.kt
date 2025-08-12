package com.mulkkam.ui.model

import com.mulkkam.domain.model.result.MulKkamError

sealed class MulKkamUiState<out T> {
    data class Success<T>(
        val data: T,
    ) : MulKkamUiState<T>()

    data object Empty : MulKkamUiState<Nothing>()

    data object Loading : MulKkamUiState<Nothing>()

    data class Failure(
        val error: MulKkamError,
    ) : MulKkamUiState<Nothing>()
}
