package com.mulkkam.ui.util

import com.mulkkam.ui.model.MulKkamUiState

fun <T> MulKkamUiState<T>?.toSuccessDataOrNull(): T? = (this as? MulKkamUiState.Success<T>)?.data
